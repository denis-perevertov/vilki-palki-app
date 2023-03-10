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
                TelegramUser user = new TelegramUser(contact.getUserId(), chatId, contact.getPhoneNumber());
                telegramUserRepository.save(user);
                log.info("User saved");

                if(appUserRepository.findByPhone(user.getPhone_number()).isEmpty()) {
                    SendMessage message = new SendMessage();
                    message.setChatId(chatId);
                    message.setText("Похоже, ваших данных нет в мобильном приложении, хотите зарегистрироваться там?");
                    message.setReplyMarkup(Buttons.yesNoMarkup());
                    try {currentMessageID = execute(message).getMessageId();} catch (TelegramApiException e){log.severe(e.getMessage());}
                    return;
                }

                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText("Успешно зарегистрировались, приятного пользования!");

                try {
                    execute(message);
                    log.info("Reply sent");
                } catch (TelegramApiException e){
                    log.severe(e.getMessage());
                }

                startBot(chatId, userId);
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

        TelegramUser user = telegramUserRepository.findByUserId(userId).orElseGet(TelegramUser::new);
        //if(!user.isSavedInDB()) registerInDB(chatId, userId, user);

        // каждое ключевое слово обрабатывается отдельным методом
        // для остальных слов ---> default echoMessage(...)
        switch(messageType) {
            case "start": startBot(chatId, userId); break;
            case "register": registerInDB(chatId, userId, user); break;
            case "help": sendHelpText(chatId); break;
            case "menu":
            case "Меню":
                openMenu(chatId); break;
            case "offers":
            case "Акции":
                offers(chatId); break;
            case "about": about(chatId); break;
            case "orders": history(chatId, userId); break;
            case "bonus": bonus(chatId, userId); break;
            case "contacts":
            case "Контакты":
                contacts(chatId); break;
            case "cabinet":
            case "Личный":
                cabinet(chatId, userId); break;
            case "profile": profile(chatId, userId); break;
            case "category": menuCategoryProcess(chatId, receivedMessage); break;
            case "item": itemInfoProcess(chatId, receivedMessage); break;
            default: echoMessage(chatId, receivedMessage);
        }
        
        
//        switch (messageType) {
//            case "start" -> startBot(chatId, userId);
//            case "register" -> registerInDB(chatId, userId, user);
//            case "help" -> sendHelpText(chatId);
//            case "Меню", "menu" -> openMenu(chatId);
//            case "Акции", "offers" -> offers(chatId);
//            case "about" -> about(chatId);
//            case "Контакты","contacts" -> contacts(chatId);
//            case "Личный", "cabinet" -> cabinet(chatId, userId);
//            case "profile" -> profile(chatId, userId);
//            case "category" -> menuCategoryProcess(chatId, receivedMessage);
//            case "item" -> itemInfoProcess(chatId, receivedMessage);
//            default -> echoMessage(chatId, receivedMessage);
//        }
    }

    private void registerInDB(long chatId, long userId, TelegramUser user) {
        AppUser appUser = appUserRepository.findByPhone(user.getPhone_number()).orElse(new AppUser());
        if(appUser.getPhone() == null) appUser.setPhone(user.getPhone_number());
        if(appUser.getName() == null) appUser.setName("TelegramUser");
        appUserRepository.save(appUser);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Успешно зарегистрированы!");
        try {execute(message);} catch (TelegramApiException e){log.severe(e.getMessage());}
        startBot(chatId, userId);
    }

    private void startBot(long chatId, long userId) {

        if(telegramUserRepository.findByUserId(userId).isEmpty()) register(chatId, userId);

        else {
            SendPhoto photoMessage = new SendPhoto();
            photoMessage.setChatId(chatId);
            photoMessage.setPhoto(new InputFile("https://komplekt.com.ua/published/publicdata/S55555CKOMPLEKT2011/attachments/SC/products_pictures/15253555_646182875563559_5649565194155892005_nee.png"));
            photoMessage.setCaption("Выберите интересующую вас опцию на клавиатуре или воспользуйтесь набором команд!\n\n"
                    + HELP_TEXT);
            photoMessage.setReplyMarkup(Buttons.replyMarkup());

            try {
                Message executedMessage = execute(photoMessage);

                if(currentMessageID != 0) deleteMessage(chatId, currentMessageID);

                currentMessageID = executedMessage.getMessageId();

                log.info("Start message sent");

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
        if(appUserRepository.findByPhone(userPhone).isEmpty()) {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText("Вы не зарегистрированы в базе данных мобильного приложения, у вас нет личного кабинета!");

            InlineKeyboardMarkup inlineMarkup = new InlineKeyboardMarkup(List.of(
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

        else {
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

    }

    private void history(long chatId, long userId) {
        String userPhone = telegramUserRepository.findByUserId(userId).orElseThrow().getPhone_number();
        AppUser user = appUserRepository.findByPhone(userPhone).orElseThrow();

        List<Order> usersOrders = user.getOrderList();

        StringBuilder orderInfo = new StringBuilder();

        usersOrders.forEach(order -> orderInfo.append("<b>ID заказа: </b> ").append(order.getId()).append("\n")
                .append("<b>Дата: </b> ").append(order.getDatetime()).append("\n")
                .append("<b>Цена: </b> ").append(order.getTotalPrice()).append("\n")
                .append("====================\n"));

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("<b>Мои заказы:</b>\n"+
                orderInfo
        );
        message.enableHtml(true);

        InlineKeyboardMarkup inlineMarkup = new InlineKeyboardMarkup(List.of(
                List.of(InlineKeyboardButton.builder().text("<-- Вернуться <--").callbackData("/cabinet").build())
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

    private void bonus(long chatId, long userId) {
        String userPhone = telegramUserRepository.findByUserId(userId).orElseThrow().getPhone_number();
        AppUser user = appUserRepository.findByPhone(userPhone).orElseThrow();

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("<b>Количество моих бонусов:</b>\n"+
                user.getBonus()
        );
        message.enableHtml(true);

        InlineKeyboardMarkup inlineMarkup = new InlineKeyboardMarkup(List.of(
                List.of(InlineKeyboardButton.builder().text("<-- Вернуться <--").callbackData("/cabinet").build())
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

    private void profile(long chatId, long userId) {
        String userPhone = telegramUserRepository.findByUserId(userId).orElseThrow().getPhone_number();
        AppUser user = appUserRepository.findByPhone(userPhone).orElseThrow();

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("<b>Информация о пользователе:</b>\n"+
                "------------------\n"+
                "\uD83D\uDC64 <i>ID: </i>" + user.getId() + "\n"+
                "\uD83E\uDDCD <i>Имя: </i>" + user.getName() + "\n"+
                "\uD83D\uDCDE <i>Номер телефона: </i>" + user.getPhone() + "\n"+
                "✉️ <i>E-mail: </i>" + user.getEmail() + "\n"+
                "\uD83C\uDF82 <i>Дата рождения: </i>" + user.getBirthdate() + "\n"+
                "\uD83D\uDC51 <i>Количество бонусов: </i>" + user.getBonus() + "\n"+
                 "------------------\n"
                );
        message.enableHtml(true);

        InlineKeyboardMarkup inlineMarkup = new InlineKeyboardMarkup(List.of(
                List.of(InlineKeyboardButton.builder().text("<-- Вернуться <--").callbackData("/cabinet").build())
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
        message.setText("Акции в этом проекте не делались");

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
        message.setText("Текст с контактами...");

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
        message.setText("Текст про нас...");

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
        File categoryPhoto = new File(uploadPath + "/categories/" + categoryId + "/" + category.getIconFileName());

        message.setPhoto(categoryPhoto.exists() ? new InputFile(categoryPhoto) : new InputFile("https://www.2dsl.ru/wp-content/uploads/kak-ispravit-oshibku-404not-found-469152c.jpg"));
        message.setChatId(chatId);
        message.setParseMode("html");
        message.setCaption("<b>Товары категории "+category.getName() + "</b>");
        message.setReplyMarkup(categoryMenu);

        try {
            Message executedMessage = execute(message);
            deleteMessage(chatId, currentMessageID);

            Integer messageID = executedMessage.getMessageId();
            currentMessageID = messageID;
            log.info("Reply sent");
            log.info("Message ID: " + messageID);
        } catch (Exception e){
            log.severe(e.getMessage());

            SendMessage error_message = new SendMessage();
            error_message.setText("Ошибка при загрузке категории!\n" + e.getMessage());
            error_message.setChatId(chatId);

            try{execute(error_message);} catch (TelegramApiException e1) {log.severe(e1.getMessage());}
        }

    }

    private void itemInfoProcess(long chatId, String receivedMessage) {

        long itemId = Long.parseLong(receivedMessage.split("[ /_.,!?=+-]")[2]);
        MenuItem item = itemService.getSingleItem(itemId);

        SendPhoto message = new SendPhoto();
        File itemPhoto = new File(uploadPath + "/" + item.getPictureFileName());

        message.setPhoto(itemPhoto.exists() ? new InputFile(itemPhoto) : new InputFile("https://www.2dsl.ru/wp-content/uploads/kak-ispravit-oshibku-404not-found-469152c.jpg"));
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
        } catch (Exception e){
            log.severe(e.getMessage());

            SendMessage error_message = new SendMessage();
            error_message.setText("Ошибка при загрузке товара!\n" + e.getMessage());
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
