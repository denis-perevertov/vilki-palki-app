package com.example.vilkipalki2.telegram;

import com.example.vilkipalki2.models.Category;
import com.example.vilkipalki2.models.MenuItem;
import com.example.vilkipalki2.services.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class Buttons {

    private static ItemService itemService;

    private static final InlineKeyboardButton START_BUTTON = new InlineKeyboardButton("Start");
    private static final InlineKeyboardButton HELP_BUTTON = new InlineKeyboardButton("Help");

    @Autowired
    public void setItemService(ItemService itemService) {
        Buttons.itemService = itemService;
    }


    public static InlineKeyboardMarkup inlineMarkup() {
        START_BUTTON.setCallbackData("/start");
        HELP_BUTTON.setCallbackData("/help");

        List<InlineKeyboardButton> rowInline = List.of(START_BUTTON, HELP_BUTTON);
        List<List<InlineKeyboardButton>> rowsInLine = List.of(rowInline);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(rowsInLine);

        return markupInline;
    }

    public static ReplyKeyboardMarkup replyMarkup() {

        KeyboardButton b1r1 = new KeyboardButton("\uD83C\uDF54 Меню");
        KeyboardButton b2r1 = new KeyboardButton("\uD83D\uDCB0 Акции");
        KeyboardButton b1r2 = new KeyboardButton("ℹ️ О нас");
        KeyboardButton b2r2 = new KeyboardButton("\uD83D\uDCDE Контакты");
        KeyboardButton b1r3 = new KeyboardButton("\uD83D\uDC64 Личный кабинет");

        KeyboardRow r1 = new KeyboardRow(List.of(b1r1, b2r1));
        KeyboardRow r2 = new KeyboardRow(List.of(b1r2, b2r2));
        KeyboardRow r3 = new KeyboardRow(List.of(b1r3));

        List<KeyboardRow> rows = List.of(r1, r2, r3);

        ReplyKeyboardMarkup markupReply = new ReplyKeyboardMarkup();
        markupReply.setKeyboard(rows);

        return markupReply;
    }

    public static InlineKeyboardMarkup menuCategoriesInlineMarkup() {
        List<InlineKeyboardButton> buttonsInRow = new ArrayList<>();
        List<List<InlineKeyboardButton>> allRows = new ArrayList<>();

        List<Category> categories = itemService.getAllCategories();

        for(Category cat : categories) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(cat.getName());
            button.setCallbackData("/category_"+cat.getId());
            buttonsInRow.add(button);
            if(buttonsInRow.size() == 3) {
                allRows.add(buttonsInRow);
                buttonsInRow = new ArrayList<>();
            }
        }
        if(buttonsInRow.size() > 0) allRows.add(buttonsInRow);

        allRows.add(List.of(InlineKeyboardButton.builder().text("Вернуться").callbackData("/start").build()));

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(allRows);

        return markupInline;
    }

    public static InlineKeyboardMarkup createMenu(List<MenuItem> itemListForCategory, String previousCallbackData) {

        List<InlineKeyboardButton> buttonsInRow = new ArrayList<>();
        List<List<InlineKeyboardButton>> allRows = new ArrayList<>();

        for(MenuItem item : itemListForCategory) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(item.getName());
            button.setCallbackData("/item_"+item.getId());
            buttonsInRow.add(button);
            if(buttonsInRow.size() == 3) {
                allRows.add(buttonsInRow);
                buttonsInRow = new ArrayList<>();
            }
        }
        if(buttonsInRow.size() > 0) allRows.add(buttonsInRow);

        buttonsInRow = new ArrayList<>();
        InlineKeyboardButton back_button = new InlineKeyboardButton("<-- Назад <--");
        back_button.setCallbackData(previousCallbackData);
        buttonsInRow.add(back_button);
        allRows.add(buttonsInRow);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(allRows);

        return markupInline;

    }
}
