package com.xakerz.SalerBots.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TelegramConfig {

    //https://api.telegram.org/bot5994666145:AAF606nhEp2R5_EBcKgDPQqq2f_Vu_JgtMQ/setWebhook?url=https://zvnvc8-46-17-104-29.ru.tuna.am
    @Value("${telegrambot.name}")
    private  String botName;
    @Value("${telegrambot.token}")
    private  String token;
    @Value("${telegrambot.webHookPath}")
    private String webHookPath;

    public String getBotName() {
        return botName;
    }

    public String getBotToken() {
        return token;
    }

    public String getWebHookPath() {
        return webHookPath;
    }
}
