package com.xakerz.SalerBots;

import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.starter.SpringWebhookBot;
import com.xakerz.SalerBots.handlers.CallbackQueryHandler;
import com.xakerz.SalerBots.handlers.MessageHandler;

import java.util.List;

public class Bot extends SpringWebhookBot {
    private String botPath;
    private String botToken;
    private String botName;

    MessageHandler messageHandler;
    CallbackQueryHandler callbackQueryHandler;


    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public CallbackQueryHandler getCallbackQueryHandler() {
        return callbackQueryHandler;
    }

    public void setCallbackQueryHandler(CallbackQueryHandler callbackQueryHandler) {
        this.callbackQueryHandler = callbackQueryHandler;
    }


    public Bot(MessageHandler messageHandler,CallbackQueryHandler callbackQueryHandler, SetWebhook setWebhook) {
        super(setWebhook);
        this.messageHandler = messageHandler;
        this.callbackQueryHandler = callbackQueryHandler;
    }


    public Bot(MessageHandler messageHandler,CallbackQueryHandler callbackQueryHandler,DefaultBotOptions options, SetWebhook setWebhook) {
        super(options, setWebhook);
        this.messageHandler = messageHandler;
        this.callbackQueryHandler = callbackQueryHandler;
    }


    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();

            return callbackQueryHandler.processCallback(callbackQuery);
        }  else {


            if(message.hasText() & message!=null)
                return messageHandler.handleMessage(message);
        }

        return null;
    }

    @Override
    public String getBotPath() {
        return botPath;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    public void setBotPath(String botPath) {
        this.botPath = botPath;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

    public void setBotName(String botName) {
        this.botName = botName;
    }

    public void sendPhotoMessage(long chatId, String photoURL) {
        SendPhoto sendPhoto = new SendPhoto();
        InputFile inputFile = new InputFile(photoURL);
        sendPhoto.setChatId(String.valueOf(chatId));
        sendPhoto.setPhoto(inputFile);


        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }

    private void sendPhotoToOtherChanel(Update update, long chatId) {
        Message message = update.getMessage();
        List<PhotoSize> photos = message.getPhoto();
        // Выбираем фотографию с наибольшим размером
        PhotoSize lastPhoto = photos.get(photos.size() - 1);
        // Получаем fileId фотографии
        String fileId = lastPhoto.getFileId();

        // Создаем объект SendPhoto с указанием chatId и fileId
        SendPhoto sendPhotoRequest = new SendPhoto();
        sendPhotoRequest.setChatId(String.valueOf(chatId));

        sendPhotoRequest.setPhoto(new InputFile(fileId));

        try {
            // Отправляем фото обратно пользователю
            execute(sendPhotoRequest);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
