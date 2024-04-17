package com.xakerz.SalerBots.handlers;

import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;


@PropertySource("classpath:application.properties")
@Component
public class MessageHandler {


    public BotApiMethod<?> handleMessage(Message message) {
        String chatId = message.getChatId().toString();
        String inputText = message.getText();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);


        if (inputText.equals("/start")) {

            sendMessage.setText("Start command has been executed");

            return sendMessage;
        } else if (inputText.equals("photo")) {

            return sendMessage;
        } else {
            sendMessage.setText(inputText.toString());
            return sendMessage;
        }

    }


    public SendPhoto handleMessagePhoto(Message message) {
        String chatId = message.getChatId().toString();
        String inputText = message.getText();
        SendPhoto sendMessage = new SendPhoto();
        sendMessage.setChatId(chatId);
        InputFile inputFile = new InputFile("https://disk.yandex.ru/i/eNx-09JOMPXYyQ");


        if (inputText.equals("photo")) {
            sendMessage.setPhoto(inputFile);
            return sendMessage;

        }
        return sendMessage;
    }


}
