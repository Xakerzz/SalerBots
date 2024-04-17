package com.xakerz.SalerBots.handlers;

public class BotConfig {
    static private final String BOT_NAME = "@SmartPsychoBot";
    static private final String BOT_TOKEN = "6877126869:AAGGgGGkiBcHLS8h2EFaFh-S_15JRnlpq-Q";


    static private final  long ADMIN_ID = 965233182L;//Марина
    static private final  long ADMIN_ID1 = 809699663L;//Рома
    static private final  long POST_CHANEL_ID = -1002053569829L;//канал постов
    static private final  long HISTORY_CHANEL_ID = -1002073426711L;//канал историй


    public static long getHISTORY_CHANEL_ID () {
        return HISTORY_CHANEL_ID;
    }
    public static long getPOST_CHANEL_ID () {
        return POST_CHANEL_ID;
    }
    public static long getADMIN_ID1 () {
        return ADMIN_ID1;
    }
    public static long getADMIN_ID () {
        return ADMIN_ID;
    }
    public static String getBOT_NAME() {
        return BOT_NAME;
    }

    public static String getBOT_TOKEN() {
        return BOT_TOKEN;
    }


}
