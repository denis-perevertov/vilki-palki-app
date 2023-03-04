package com.example.vilkipalki2.telegram;

import com.example.vilkipalki2.config.TelegramBotConfig;
import com.example.vilkipalki2.models.*;
import com.example.vilkipalki2.repos.AppUserRepository;
import com.example.vilkipalki2.repos.TelegramUserRepository;
import com.example.vilkipalki2.services.ItemService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.vilkipalki2.telegram.BotCommands.HELP_TEXT;
import static com.example.vilkipalki2.telegram.BotCommands.LIST_OF_COMMANDS;

@Log
@Component
public class MyBot extends TelegramLongPollingBot {

    public TelegramBotConfig config;

    @Autowired
    private ItemService itemService;

    @Autowired
    private TelegramUserRepository telegramUserRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Value("${upload.path}")
    private String uploadPath;

    public Integer currentMessageID = 0;

    public MyBot(TelegramBotConfig config) {
        super(config.getToken());
        this.config = config;
        try {
            this.execute(new SetMyCommands(LIST_OF_COMMANDS, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e){
            log.severe(e.getMessage());
        }

    }

    @Override
    public void onUpdateReceived(Update update) {
        long chatId = 0;
        long userId = 0;
        String userName = null;
        String receivedMessage;

        // обработка сообщений
        if(update.hasMessage()) {

            chatId = update.getMessage().getChatId();
            userId = update.getMessage().getFrom().getId();
            userName = update.getMessage().getFrom().getUserName();

            log.info("User info: ID " + userId + ", chat ID " + chatId + ", user Name " + userName);

            //если сообщение было сугубо текстовое
            if (update.getMessage().hasText()) {
                receivedMessage = update.getMessage().getText();
                log.info("User "+userId+" sent message: " + receivedMessage);
                botAnswerUtils(receivedMessage, chatId, userId, userName);
            }
            //если сообщение содержит контакт ТГ (регистрация в боте)
            else if (update.getMessage().hasContact()) {

                //передача своего номера телефона для регистрации в БД

                Contact contact = update.getMessage().getContact();

                log.info("User " + userId + " sent his contact info, phone number: " + contact.getPhoneNumber());
                log.info("Saving user");
                TelegramUser user = new TelegramUser(contact.getUserId(), contact.getPhoneNumber());
                telegramUserRepository.save(user);
                log.info("User saved");

                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText("Успешно зарегистрировались, приятного пользования!");

                try {
                    execute(message);
                    log.info("Reply sent");
                } catch (TelegramApiException e){
                    log.severe(e.getMessage());
                }

                startBot(chatId, userId, userName);
            }

        // обработка callbackData из Inline buttons сообщений
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            userId = update.getCallbackQuery().getFrom().getId();
            userName = update.getCallbackQuery().getFrom().getFirstName();
            receivedMessage = update.getCallbackQuery().getData();

            log.info("Callback data from inline button: " + receivedMessage + ", user ID " + userId + ", chat ID " + chatId);

            botAnswerUtils(receivedMessage, chatId, userId, userName);
        }
    }

    private void botAnswerUtils(String receivedMessage, long chatId, long userId, String userName) {

        // выделение типа сообщения по ключевым словам
        // "/start" ---> "start"
        // "/category_13" ---> "category"
        String messageType = receivedMessage.charAt(0) == '/' ? receivedMessage.split("[ /_.,!?=+-]")[1] : receivedMessage;
        log.info("Message type of the sent message: " + messageType);

        // каждое ключевое слово обрабатывается отдельным методом
        // для остальных слов ---> default echoMessage(...)
        switch (messageType) {
            case "start" -> startBot(chatId, userId, userName);
            case "help" -> sendHelpText(chatId);
            case "Меню", "menu" -> openMenu(chatId);
            case "Акции", "offers" -> offers(chatId);
            case "about" -> about(chatId);
            case "Контакты","contacts" -> contacts(chatId);
            case "Личный", "cabinet" -> cabinet(chatId, userId);
            case "category" -> menuCategoryProcess(chatId, receivedMessage);
            case "item" -> itemInfoProcess(chatId, receivedMessage);
            default -> echoMessage(chatId, receivedMessage);
        }
    }

    private void startBot(long chatId, long userId, String userName) {

        if(telegramUserRepository.findByUserId(userId).isEmpty()) register(chatId, userId);

        else {
            SendPhoto photoMessage = new SendPhoto();
            photoMessage.setChatId(chatId);
            photoMessage.setPhoto(new InputFile("https://i.redd.it/0nkapmgaoul91.jpg"));
            photoMessage.setCaption("Выберите интересующую вас опцию на клавиатуре или воспользуйтесь набором команд!\n\n"
                    + HELP_TEXT);
            photoMessage.setReplyMarkup(Buttons.replyMarkup());

            try {
                execute(photoMessage);
                log.info("Picture sent");
            } catch (TelegramApiException e){
                log.severe(e.getMessage());
            }
        }

    }

    private void register(long chatId, long userId) {

        // Первичная регистрация , номер телефона и другую инфу контакта ТГ можно получить только при его согласии
        // (при использовании KeyboardButton)

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Зарегистрируйтесь в боте для начала работы!");

        KeyboardButton registerButton = new KeyboardButton();
        registerButton.setText("Зарегистрироваться");
        registerButton.setRequestContact(true);
        KeyboardRow row = new KeyboardRow(List.of(registerButton));
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(List.of(row));

        message.setReplyMarkup(replyKeyboardMarkup);

        try {
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e){
            log.severe(e.getMessage());
        }

        // если все прошло успешно - отправляется сообщение с контактом

    }

    private void sendHelpText(long chatId){
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(BotCommands.HELP_TEXT);

        Message msg = new Message();
        MessageId id = new MessageId();

        try {
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e){
            log.severe(e.getMessage());
        }
    }

    private void openMenu(long chatId) {

        SendPhoto message = new SendPhoto();
        message.setChatId(chatId);
        message.setParseMode("html");
        message.setCaption("<b><i>Выбери категорию из списка: </i></b>");
        message.setPhoto(new InputFile("https://foodhub.scene7.com/is/image/woolworthsltdprod/2004-easy-pepperoni-pizza:Mobile-1300x1150"));
        message.setReplyMarkup(Buttons.menuCategoriesInlineMarkup());

        try {
            Message executedMessage = execute(message);
            if(currentMessageID != 0) deleteMessage(chatId, currentMessageID);

            Integer messageID = executedMessage.getMessageId();
            currentMessageID = messageID;
            log.info("Reply sent");
            log.info("Message ID: " + messageID);
        } catch (TelegramApiException e){
            log.severe(e.getMessage());
        }


    }

    private void cabinet(long chatId, long userId) {

        // Личный кабинет пользователя

        String userPhone = telegramUserRepository.findByUserId(userId).orElseThrow().getPhone_number();
        AppUser user = appUserRepository.findByPhone(userPhone).orElseThrow();

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("<b>Это ваш личный кабинет!</b>\nВыберите опцию на клавиатуре, чтобы посмотреть интересюущие вас данные");
        message.enableHtml(true);

        InlineKeyboardMarkup inlineMarkup = new InlineKeyboardMarkup(List.of(
                List.of(InlineKeyboardButton.builder().text("Профиль").callbackData("/profile").build()),
                List.of(InlineKeyboardButton.builder().text("История заказов").callbackData("/orders").build()),
                List.of(InlineKeyboardButton.builder().text("Мои бонусы").callbackData("/bonus").build()),
                List.of(InlineKeyboardButton.builder().text("<-- Вернуться <--").callbackData("/start").build())
        ));

        message.setReplyMarkup(inlineMarkup);

        try {
            Message executedMessage = execute(message);
            if(currentMessageID != 0) deleteMessage(chatId, currentMessageID);

            Integer messageID = executedMessage.getMessageId();
            currentMessageID = messageID;
            log.info("Reply sent");
            log.info("Message ID: " + messageID);
        } catch (TelegramApiException e){
            log.severe(e.getMessage());
        }
    }

    private void offers(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Функция еще не сделана");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(List.of(
                List.of(InlineKeyboardButton.builder().text("Вернуться").callbackData("/start").build())
        ));

        message.setReplyMarkup(inlineKeyboardMarkup);

        try {
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e) {
            log.severe(e.getMessage());
        }
    }

    private void contacts(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Функция еще не сделана");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(List.of(
                List.of(InlineKeyboardButton.builder().text("Вернуться").callbackData("/start").build())
        ));

        message.setReplyMarkup(inlineKeyboardMarkup);

        try {
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e) {
            log.severe(e.getMessage());
        }
    }

    private void about(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Функция еще не сделана");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(List.of(
                List.of(InlineKeyboardButton.builder().text("Вернуться").callbackData("/start").build())
        ));

        message.setReplyMarkup(inlineKeyboardMarkup);

        try {
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e) {
            log.severe(e.getMessage());
        }

    }

    private void menuCategoryProcess(long chatId, String receivedMessage) {

        long categoryId = Long.parseLong(receivedMessage.split("[ /_.,!?=+-]")[2]);
        Category category = itemService.getSingleCategoryById(categoryId);
        List<MenuItem> itemListForCategory = category.getItemList();
        InlineKeyboardMarkup categoryMenu = Buttons.createMenu(itemListForCategory, "/menu");

        SendPhoto message = new SendPhoto();
        message.setChatId(chatId);
        message.setParseMode("html");
        message.setCaption("<b>Товары категории "+category.getName() + "</b>");
        message.setPhoto(new InputFile(new File(uploadPath + "/categories/" + categoryId + "/" + category.getIconFileName())));
        message.setReplyMarkup(categoryMenu);

        try {
            Message executedMessage = execute(message);
            deleteMessage(chatId, currentMessageID);

            Integer messageID = executedMessage.getMessageId();
            currentMessageID = messageID;
            log.info("Reply sent");
            log.info("Message ID: " + messageID);
        } catch (TelegramApiException e){
            log.severe(e.getMessage());
        }

    }

    private void itemInfoProcess(long chatId, String receivedMessage) {

        long itemId = Long.parseLong(receivedMessage.split("[ /_.,!?=+-]")[2]);
        MenuItem item = itemService.getSingleItem(itemId);

        SendPhoto message = new SendPhoto();
        message.setChatId(chatId);

        message.setParseMode("html");

        String itemInfo = "<b>" + item.getName() + "</b>" + "\n" +
                "<b>Цена: </b>" + item.getPrice() + "\n" +
                "--- === ---" + "\n" +
                "<b>Жиры: </b>" + item.getFats() + "\n" +
                "<b>Углероды: </b>" + item.getCarbons() + "\n" +
                "<b>Белки: </b>" + item.getPrice() + "\n" +
                "<b>Калории: </b>" + item.getCalories() + "\n" +
                "--- === ---" + "\n" +
                "<b>Ингредиенты: </b>" + item.getIngredients().stream()
                .map(Ingredient::toString)
                .collect(Collectors.joining(","));

        message.setCaption(itemInfo);

        message.setPhoto(new InputFile(new File(uploadPath + "/" + item.getPictureFileName())));

        InlineKeyboardButton back_button = new InlineKeyboardButton("<-- Назад <--");
        back_button.setCallbackData("/category_"+item.getCategory().getId());

        List<InlineKeyboardButton> buttonsInRow = new ArrayList<>();
        List<List<InlineKeyboardButton>> allRows = new ArrayList<>();

        buttonsInRow.add(back_button);
        allRows.add(buttonsInRow);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(allRows);

        message.setReplyMarkup(inlineKeyboardMarkup);

        try {
            Message executedMessage = execute(message);
            deleteMessage(chatId, currentMessageID);

            Integer messageID = executedMessage.getMessageId();
            currentMessageID = messageID;
            log.info("Reply sent");
            log.info("Message ID: " + messageID);
        } catch (TelegramApiException e){
            log.severe(e.getMessage());

            SendMessage error_message = new SendMessage();
            error_message.setText("Ошибка при загрузке товара!");
            error_message.setChatId(chatId);

            try{execute(error_message);} catch (TelegramApiException e1) {log.severe(e1.getMessage());}
        }

    }



    private void deleteMessage(long chatId, Integer messageID) {

        DeleteMessage message = new DeleteMessage();
        message.setChatId(chatId);
        message.setMessageId(messageID);

        try {
            execute(message);
            log.info("Message with ID " +messageID + " deleted");
        } catch (TelegramApiException e){
            log.severe(e.getMessage());
        }
    }
    private void editMessage(long chatId, Integer messageID) {}
    private void echoMessage(long chatId, String receivedMessage) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Непонятное боту сообщение, попробуйте еще раз: \n" + receivedMessage + "\n\n" + HELP_TEXT);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }
    public String getBotToken() {
        return config.getToken();
    }
}
