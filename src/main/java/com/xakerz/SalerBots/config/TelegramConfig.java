package com.xakerz.SalerBots.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TelegramConfig {

    //https://api.telegram.org/bot5994666145:AAF606nhEp2R5_EBcKgDPQqq2f_Vu_JgtMQ/setWebhook?url=https://c3bf-2a04-5200-fff5-00-7f6.ngrok-free.app
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
