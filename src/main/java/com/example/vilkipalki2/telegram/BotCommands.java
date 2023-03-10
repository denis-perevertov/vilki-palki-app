package com.example.vilkipalki2.telegram;

import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.List;
import java.util.stream.Collectors;

public interface BotCommands {

    List<BotCommand> LIST_OF_COMMANDS = List.of(
            new BotCommand("/start", "начать работу"),
            new BotCommand("/help", "информация про бота"),
            new BotCommand("/menu", "показать меню"),
            new BotCommand("/offers", "акции"),
            new BotCommand("/cabinet", "кабинет пользователя"),
            new BotCommand("/about", "о нас"),
            new BotCommand("/contacts", "контакты")
    );

    String HELP_TEXT = "Список команд, которые понимает бот: \n" + LIST_OF_COMMANDS.stream()
            .map((command) -> command.getCommand() + " - " + command.getDescription())
            .collect(Collectors.joining("\n")) + "\n\nВ данный момент кнопочные команды на русском не работают из-за проблем с кодировкой";

}
