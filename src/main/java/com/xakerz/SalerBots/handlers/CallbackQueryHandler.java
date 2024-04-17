package com.xakerz.SalerBots.handlers;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class CallbackQueryHandler {
    public BotApiMethod<?>processCallback(CallbackQuery callbackQuery){
        String chatId = callbackQuery.getMessage().getChatId().toString();
        String data = callbackQuery.getData();

        return null;
    }
}
