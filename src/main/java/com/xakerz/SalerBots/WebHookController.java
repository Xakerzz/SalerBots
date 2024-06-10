package com.xakerz.SalerBots;

import SaveSerFiles.MapFileHandler;
import com.xakerz.SalerBots.handlers.BotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.payments.LabeledPrice;
import org.telegram.telegrambots.meta.api.objects.payments.PreCheckoutQuery;
import org.telegram.telegrambots.meta.api.objects.payments.SuccessfulPayment;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;


@RestController
public class WebHookController {
    public static Map<Long, Client> clients = new HashMap<>();
    public static Map<Long, Integer> lastMessageIds = new HashMap<>();
    public static Map<Long, Integer> lastMessageIdsPhoto = new HashMap<>();
    public static Map<Long, Integer> adminPanelMessageIdsToAdmin = new HashMap<>();
    public static Map<Long, Integer> lastMessageIdsMessagePay = new HashMap<>();
    public static Map<Long, Integer> lastMessageIdsToAdmin = new HashMap<>();
    public static Map<Long, ArrayList<String>> listClientMessages = new HashMap<>();
    public static Map<Long, ArrayList<Integer>> listMessagesForDelete = new HashMap<>();

    static ClientAdmin Admin1 = new ClientAdmin("Роман", "Роман", BotConfig.getADMIN_ID1(), false);
    private static final String SHOP_ID = "506751";
    private static final String SHOP_ARTICLE_ID = "538350";
    static Bot bot;

    @Autowired
    public WebHookController(Bot bot) {
        WebHookController.bot = bot;
    }


    @PostMapping("/")
    public void onUpdateReceived(@RequestBody Update update) throws TelegramApiException {
        Client client;
        Message message = update.getMessage();


        if (update.hasMessage() && message.hasText()) {


            if (clients.containsKey(message.getChatId())) {
                client = clients.get(message.getChatId());
                String name = message.getFrom().getFirstName();
                String nickName = "@" + message.getFrom().getUserName();


                if (!name.equals(client.getName())) {
                    client.setName(name);
                } else if (!nickName.equals(client.getNickName())) {
                    client.setNickName(nickName);
                }


            } else {
                String name = update.getMessage().getFrom().getFirstName();
                String nickName = update.getMessage().getFrom().getUserName();
                Long idClient = update.getMessage().getFrom().getId();

                client = new Client(name, nickName, idClient, false);
                clients.put(client.getIdClient(), client);

            }


            if (message.hasText() && client.isNameInfo()) {
                deleteMessageText(client.getIdClient(), lastMessageIds.get(client.getIdClient()));
                client.setNameForInfo(message.getText());
                deleteMessageText(client.getIdClient(), message.getMessageId());
                messageText(client.getIdClient(), "Ваше имя принято. \nВведите ваш возраст.");
                client.setAgeInfo(true);
                client.setNameInfo(false);
            } else if (message.hasText() && client.isAgeInfo()) {
                deleteMessageText(client.getIdClient(), lastMessageIds.get(client.getIdClient()));
                try {
                    client.setAgeForInfo(Integer.parseInt(message.getText()));
                    deleteMessageText(client.getIdClient(), message.getMessageId());

                } catch (NumberFormatException e) {
                    messageText(client.getIdClient(), "Вы ввели не число, введите пожалуйста число.");
                    return;
                }
                messageText(client.getIdClient(), "Ваш возраст записан.\n\n\n вас зовут " + client.getNameForInfo() + " ваш возраст- " + client.getAgeForInfo(), "⏪назад", "functions");
                client.setAgeInfo(false);
            }


            if (client.getIdToMessage() != 0 && update.hasMessage()) {
                System.out.println(client.getIdToMessage() + " " + clients.get(client.getIdToMessage()).getIdToMessage());
                messageText(client.idToMessage, client.getName() + ": " + message.getText());
            }

            if (message.getText().equals("/start")) {
                start(client, message);


            } else if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().equals("photo")) {
                sendPhotoMessage(Long.parseLong(String.valueOf(update.getMessage().getChatId())), "https://disk.yandex.ru/i/eNx-09JOMPXYyQ");


            } else if (update.hasMessage() && client.isSaveMessages()) {
                String clientMessage = message.getText();
                client.clientMessages.add(clientMessage);
                listClientMessages.put(client.getIdClient(), client.clientMessages);
            }
        } else if (update.hasCallbackQuery()) {

            CallbackQuery callbackQuery = update.getCallbackQuery();
            String callBackData = callbackQuery.getData();

            if (clients.containsKey(callbackQuery.getMessage().getChatId())) {
                client = clients.get(callbackQuery.getMessage().getChatId());
                String name = callbackQuery.getFrom().getFirstName();
                String nickName = "@" + callbackQuery.getFrom().getUserName();
                client.setMessageId(callbackQuery.getMessage().getMessageId());


                if (!name.equals(client.getName())) {
                    client.setName(name);
                } else if (!nickName.equals(client.getNickName())) {
                    client.setNickName(nickName);
                }


            } else {
                String name = callbackQuery.getFrom().getFirstName();
                String nickName = callbackQuery.getFrom().getUserName();
                Long idClient = callbackQuery.getMessage().getChatId();


                client = new Client(name, nickName, idClient, false);
                client.setMessageId(callbackQuery.getMessage().getMessageId());
                clients.put(client.getIdClient(), client);

            }
            if (callBackData.equals("test")) {
                deleteMessageText(client.getIdClient(), lastMessageIds.get(client.getIdClient()));
                deleteMessageText(client.getIdClient(), lastMessageIdsPhoto.get(client.getIdClient()));
            } else if (callBackData.equals("doOrder")) {
                deleteMessageText(client.getIdClient(), lastMessageIds.get(client.getIdClient()));
                deleteMessageText(client.getIdClient(), lastMessageIdsPhoto.get(client.getIdClient()));
                client.setSaveMessages(true);
                clients.put(client.getIdClient(), client);
                messageText(client.getIdClient(), "Ваша заявка отправлена, вам скоро напишут\uD83E\uDEF6", "⏪назад", "back1");

                messageTextStory(BotConfig.getADMIN_ID1(), "Заявка от  " + client.getNickName() + "\n" + client.getName() + "\n" + "\uD83E\uDEF6Новая консультация\uD83E\uDEF6\n\n" + client.getInfo(), "Закрыть консультацию", "Начать консультацию", "cancelCons " + client.getIdClient(), "doOrder" + client.getIdClient());
            } else if (callBackData.equals("back1")) {
                deleteMessageText(client.getIdClient(), lastMessageIds.get(client.getIdClient()));
                if (!lastMessageIdsMessagePay.isEmpty() && lastMessageIdsMessagePay.get(client.getIdClient()) != null) {
                    deleteMessageText(client.getIdClient(), lastMessageIdsMessagePay.get(client.getIdClient()));
                }

                start2(client, message);
            } else if (callBackData.contains("doOrder") && (client.getIdClient() == BotConfig.getADMIN_ID() || client.getIdClient() == BotConfig.getADMIN_ID1())) {
                long idclient = Long.parseLong(callBackData.replaceAll("doOrder", "").trim());
                Client client1 = clients.get(idclient);
                long adminId = client.getIdClient();

                if (idclient != client.getIdToMessage() && !client1.isQuestionToAdmin()) {
//

                    if (adminId == Admin1.getIdClient()) {
                        if (lastMessageIdsToAdmin.get(client.getIdClient()) != null) {
                            deleteMessageText(client.getIdClient(), lastMessageIdsToAdmin.get(client.getIdClient()));
                        }
                        messageText(client.getIdClient(), "Можно писать клиенту");
                        Admin1.setIdToMessage(idclient);
                        client1.setIdToMessage(client.getIdClient());
                        client1.setQuestionToAdmin(true);
                        clients.put(idclient, client1);

                        client.setIdToMessage(idclient);
                        deleteMessageText(client.getIdClient(), lastMessageIdsToAdmin.get(client.getIdClient() + idclient));

                        editMessageText(Admin1.getIdClient(), "Заявка от  " + client1.getNickName() + "\n" + client1.getName() + "\n" + "‼\uFE0FВ работе‼\uFE0F\n\n" + client1.getInfo(), "Закрыть консультацию", "Начать консультацию", "cancelCons " + idclient, "doOrder" + idclient);
                        if (!listClientMessages.get(client1.getIdClient()).isEmpty()) {
                            ArrayList<String> temp = listClientMessages.get(client1.getIdClient());
                            for (String str : temp) {
                                messageText(BotConfig.getADMIN_ID1(), client1.getName() + ": " + str);

                            }
                            listClientMessages.remove(client1.getIdClient());
                            client1.setSaveMessages(false);
                            clients.put(client1.getIdClient(), client1);
                        }


                        System.out.println(idclient);

                    }
                } else {
                    if (lastMessageIdsToAdmin.get(client.getIdClient()) != null) {
                        deleteMessageText(client.getIdClient(), lastMessageIdsToAdmin.get(client.getIdClient()));
                    }
                    messageText(client.getIdClient(), "\uD83D\uDC4CКлиент уже в работе\uD83D\uDC4C");
                }
                System.out.println(client.getIdToMessage() + " " + client1.getIdToMessage() + " " + client.isQuestionToAdmin() + " " + client1.isQuestionToAdmin());
            } else if (callBackData.contains("cancelCons")) {
                long idclient = Long.parseLong(callBackData.replaceAll("cancelCons", "").trim());
                Client client1 = clients.get(idclient);
                long adminId = client.getIdClient();
                deleteMessageText(client.getIdClient(), lastMessageIds.get(client.getIdClient()));
                messageText(client1.getIdClient(), "Диалог с вами закрыт, но вы можете отправлять сообщения разработчику, чтобы он их увидел подайте заявку еще раз, спасибо", "⏪назад", "back1");
                client1.setSaveMessages(true);
                clients.put(client1.getIdClient(), client1);

                if (adminId == Admin1.getIdClient()) {

                    deleteMessageText(client.getIdClient(), lastMessageIdsToAdmin.get(BotConfig.getADMIN_ID1() + idclient));

                    editMessageText(Admin1.getIdClient(), "Заявка от  " + client1.getNickName() + "\n" + client1.getName() + "\n" + "✅Заявка закрыта✅\n\n" + client1.getInfo(), "Закрыть консультацию", "Начать консультацию", "cancelCons" + idclient, "doOrder" + idclient);

                }
                client1.setQuestionToAdmin(false);
                clients.get(idclient).setIdToMessage(0);
                client.setIdToMessage(0);
                clients.put(client1.getIdClient(), client1);
            } else if (callBackData.contains("functions")) {
                deleteMessageText(client.getIdClient(), lastMessageIds.get(client.getIdClient()));
                deleteMessageText(client.getIdClient(), lastMessageIdsPhoto.get(client.getIdClient()));

                messageText(client.getIdClient(), "\uD83E\uDD16Базовые функции:",
                        "✅Оплата", "✅Сбор информации", "✅Удаление сообщений", "✅Изменение сообщений", "✅Отправка сообщений с задержкой", "✅Общение с клиентами", "⏪назад",
                        "pay", "collectInfo", "deleteMessage", "changeMessage", "schedulerMessage", "chatWithClient", "back1");
            } else if (callBackData.contains("portfolio")) {
                deleteMessageText(client.getIdClient(), lastMessageIds.get(client.getIdClient()));
                deleteMessageText(client.getIdClient(), lastMessageIdsPhoto.get(client.getIdClient()));

                messageText(client.getIdClient(), "\uD83E\uDD16Портфолио:",
                        "✅Бот-тест", "✅VPN бот", "✅ПсихоБот", "✅QrCode бот", "✅Тренажер умножения", "✅Шар судьбы бот", "⏪назад",
                        "bot-test", "vpnBot", "psychoBot", "qrBot", "multiBot", "magicBall", "back1");
            } else if (callBackData.contains("pay")) {
                deleteMessageText(client.getIdClient(), lastMessageIds.get(client.getIdClient()));

                SendInvoice sendInvoice = new SendInvoice();
                sendInvoice.setChatId(client.getIdClient());
                sendInvoice.setTitle("Тестовый платеж");
                sendInvoice.setDescription("Ваш товар");
                sendInvoice.setPayload("custom_payload");
                sendInvoice.setProviderToken("381764678:TEST:80071");
                sendInvoice.setStartParameter("test");
                sendInvoice.setCurrency("RUB");
                sendInvoice.setPrices(Arrays.asList(new LabeledPrice[]{new LabeledPrice("Руб", 99900)}));

                //sendInvoice.setProviderData("{\"shop_id\": " + SHOP_ID + ", \"shop_article_id\": " + SHOP_ARTICLE_ID + "}");// in cents

                try {
                    Message sentMessage = bot.execute(sendInvoice);
                    lastMessageIdsMessagePay.put(client.getIdClient(), sentMessage.getMessageId());

                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                messageText(client.getIdClient(), "Данные карты для тестового платежа:\n Номер карты: 1111 1111 1111 1026\nСрок действия: 12/22\nCVV код: 000 \n\n\nДля возврата в меню функций нажмите \"назад\"", "⏪Назад", "functions");

            } else if (callBackData.contains("collectInfo")) {
                deleteMessageText(client.getIdClient(), lastMessageIds.get(client.getIdClient()));
                messageText(client.getIdClient(), "Введите ваше имя:");
                client.setNameInfo(true);


            } else if (callBackData.contains("deleteMessage")) {
                deleteMessageText(client.getIdClient(), lastMessageIds.get(client.getIdClient()));
                messageText(client.getIdClient(), "Сообщение для удаления, нажмите на кнопку \"Удалить\"", "Удалить", "⏪Назад", "delete", "functions");


            } else if (callBackData.contains("delete")) {
                deleteMessageText(client.getIdClient(), lastMessageIds.get(client.getIdClient()));

                messageText(client.getIdClient(), "Сообщение удалено", "⏪Назад", "functions");

            } else if (callBackData.contains("changeMessage")) {
                deleteMessageText(client.getIdClient(), lastMessageIds.get(client.getIdClient()));
                messageText(client.getIdClient(), "Сообщение для изменения, нажмите на кнопку \"Изменить\"", "Изменить", "⏪Назад", "change", "functions");


            } else if (callBackData.contains("change")) {


                editeMessage(client.getIdClient(), client.getMessageId(), "Сообщение изменено", "⏪Назад", "functions");

            } else if (callBackData.contains("schedulerMessage")) {
                deleteMessageText(client.getIdClient(), lastMessageIds.get(client.getIdClient()));
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sendTypingStatus(client.getIdClient());
                        try {
                            Thread.sleep(2000);
                            messageTextSchedule(client.getIdClient(), "Таким образом можно", client);
                            Thread.sleep(2000);
                            messageTextSchedule(client.getIdClient(), "посылать сообщения с задержкой", client);
                            Thread.sleep(2000);
                            messageTextSchedule(client.getIdClient(), "или в запланированное время", client);
                            Thread.sleep(2000);
                            messageTextSchedule(client.getIdClient(), "Это очень удобно", client);
                            Thread.sleep(2000);
                            messageTextSchedule(client.getIdClient(), "Создаётся иллюзия ", client);
                            Thread.sleep(2000);
                            messageTextSchedule(client.getIdClient(), "Общения с реальным человеком", client);
                            Thread.sleep(2000);
                            messageTextSchedule(client.getIdClient(), "А потом можно все удалить)", client);
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        ArrayList<Integer> temp = listMessagesForDelete.get(client.getIdClient());

                        for (Integer messageId : temp.reversed()) {
                            for (int i = 3; i > 0; i--) {
                                messageText(client.getIdClient(), String.valueOf(i));

                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                                if (!lastMessageIds.isEmpty()) {
                                    deleteMessageText(client.getIdClient(), lastMessageIds.get(client.getIdClient()));
                                } else {
                                    deleteMessageText(client.getIdClient(), lastMessageIds.get(client.getIdClient()));
                                    break;
                                }
                            }
                            deleteMessageText(client.getIdClient(), messageId);

                        }
                        listMessagesForDelete.get(client.getIdClient()).removeAll(client.listMessagesForDelete);
                        messageText(client.getIdClient(), "Для возврата в меню функций нажмите \"Назад\"", "⏪Назад", "functions");

                    }
                });
                thread.start();


            } else if (callBackData.contains("chatWithClient")) {
                deleteMessageText(client.getIdClient(), lastMessageIds.get(client.getIdClient()));

                messageText(client.getIdClient(), "Можно реализовать чат администратора с клиентом, либо сделать подобие телефонной АТС с возможность распределения входящих сообщений среди ваших сотрудников.", "⏪Назад", "functions");

            } else if (callBackData.contains("bot-test")) {
                deleteMessageText(client.getIdClient(), lastMessageIds.get(client.getIdClient()));
                messageTextAndPhoto(client.getIdClient(), "https://disk.yandex.ru/i/OApoXvhEe4-H6g", "@ExtraSoftSkillsBot - данный бот проводит опрос клиента, на основании его ответов выдаёт результат. Воронка продаж с возможностью консультирования клиентов прямо в боте.", "⏪Назад", "portfolio");


            } else if (callBackData.contains("vpnBot")) {
                deleteMessageText(client.getIdClient(), lastMessageIds.get(client.getIdClient()));
                messageTextAndPhoto(client.getIdClient(), "https://disk.yandex.ru/i/QSYHN_R6TFqykQ", "@TrrustVPNbot - данный бот продает ключи VPN.", "⏪Назад", "portfolio");


            } else if (callBackData.contains("psychoBot")) {
                deleteMessageText(client.getIdClient(), lastMessageIds.get(client.getIdClient()));
                messageTextAndPhoto(client.getIdClient(), "https://disk.yandex.ru/i/niK8h1Q-XiaJ1w", "@SmartPsychoBot - данный бот позволяет получить консультацию психолога прямо в боте, также оставить анонимную историю для публикации в сообществе для получения советов в жизненной ситуации изложенной в истории. Также бот публикует посты в сообществе по расписанию.", "⏪Назад", "portfolio");


            } else if (callBackData.contains("qrBot")) {
                deleteMessageText(client.getIdClient(), lastMessageIds.get(client.getIdClient()));
                messageTextAndPhoto(client.getIdClient(), "https://disk.yandex.ru/i/tWy2-0Ag4Jelmg", "@QrCoadeBot - данный бот создаёт QrCode из текста присланного пользователем.", "⏪Назад", "portfolio");


            } else if (callBackData.contains("multiBot")) {
                deleteMessageText(client.getIdClient(), lastMessageIds.get(client.getIdClient()));
                messageTextAndPhoto(client.getIdClient(), "https://disk.yandex.ru/i/I_IFMkq2lTjKBg", "@EasyMultiplicationTableBot - данный бот помогает выучить таблицу умножения.", "⏪Назад", "portfolio");


            } else if (callBackData.contains("magicBall")) {
                deleteMessageText(client.getIdClient(), lastMessageIds.get(client.getIdClient()));
                messageTextAndPhoto(client.getIdClient(), "https://disk.yandex.ru/i/YG1xdrTbkTTFdA", "@MagicFromBallbot - данный бот на основании вопроса пользователя даёт предсказание.", "⏪Назад", "portfolio");


            }
        } else if (update.hasMessage() && update.getMessage().hasPhoto()) {
            if (Admin1.idToMessage == message.getChatId() || Admin1.getIdClient() == message.getChatId()) {
                Client client1 = clients.get(message.getChatId());
                sendPhotoToOtherChanel(update, client1.getIdToMessage());
                System.out.println(client1.getIdToMessage());
            }
        } else if (update.hasMessage() && update.getMessage().hasDocument()) {
            if (Admin1.idToMessage == message.getChatId() || Admin1.getIdClient() == message.getChatId()) {
                Client client1 = clients.get(message.getChatId());
                sendDocument(update, client1.getIdToMessage());
                System.out.println(client1.getIdToMessage());
            }
        } else if (update.hasPreCheckoutQuery()) {
            // Process pre-checkout query
            PreCheckoutQuery preCheckoutQuery = update.getPreCheckoutQuery();
            String queryId = preCheckoutQuery.getId();

            AnswerPreCheckoutQuery answerPreCheckoutQuery = new AnswerPreCheckoutQuery();
            answerPreCheckoutQuery.setPreCheckoutQueryId(queryId);
            answerPreCheckoutQuery.setOk(true);

            try {

                // Answer the pre-checkout query
                bot.execute(answerPreCheckoutQuery);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if (update.hasMessage() && update.getMessage().hasPhoto()) {
            sendPhotoToOtherChanel(update, update.getMessage().getChatId());
        } else if (update.hasMessage() && update.getMessage().hasVideo()) {
            sendVideoToOtherChanel(update);
        } else {
            if (update.hasMessage()) {
                SuccessfulPayment successfulPayment = update.getMessage().getSuccessfulPayment();

                if (successfulPayment != null) {
                    // Ваша логика для обработки успешного платежа
                    String currency = successfulPayment.getCurrency();
                    int totalAmount = successfulPayment.getTotalAmount();
                    System.out.println("Payment successful! Currency: " + currency + ", Total amount: " + totalAmount);
                }

            }
        }
        MapFileHandler.saveMaps();
    }

    public void start(Client client, Message message) {
        sendPhoto(client, "Наш бот приветствует вас\uD83D\uDC4B", "https://disk.yandex.ru/i/Oyl4xNaS72_PiA");
        deleteMessageText(client.getIdClient(), message.getMessageId());
        String hi = "Здесь вы сможете ознакомиться с базовым функционалом который будет в вашем боте, функции нужные вам мы добавим по вашему желанию.";
        String[] arrayStr = hi.split(" ");
        messageText(client.getIdClient(), arrayStr[0], "Заказать бота", "Возможности бота", "Портфолио",
                "doOrder", "functions", "portfolio");


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                sendTypingStatus(client.getIdClient());
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                StringBuilder temp = new StringBuilder();
                for (String str : arrayStr) {
                    temp.append(str + " ");
                    try {
                        Thread.sleep(2);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    editeMessage(client.getIdClient(), lastMessageIds.get(client.getIdClient()), temp.toString(),
                            "Заказать бота", "Возможности бота", "Портфолио",
                            "doOrder", "functions", "portfolio");

                }
            }
        });
        thread.start();
    }

    public void start2(Client client, Message message) {
        sendPhoto(client, "С возвращением\uD83D\uDC4B", "https://disk.yandex.ru/i/Oyl4xNaS72_PiA");

        String hi = "Здесь вы сможете ознакомиться с базовым функционалом который будет в вашем боте, функции нужные вам мы добавим по вашему желанию.";
        String[] arrayStr = hi.split(" ");
        messageText(client.getIdClient(), arrayStr[0], "Заказать бота", "Возможности бота", "Портфолио",
                "doOrder", "functions", "portfolio");


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                sendTypingStatus(client.getIdClient());
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                StringBuilder temp = new StringBuilder();
                for (String str : arrayStr) {
                    temp.append(str + " ");
                    try {
                        Thread.sleep(2);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    editeMessage(client.getIdClient(), lastMessageIds.get(client.getIdClient()), temp.toString(),
                            "Заказать бота", "Возможности бота", "Портфолио",
                            "doOrder", "functions", "portfolio");

                }
            }
        });
        thread.start();
    }

    public void sendPhotoMessage(long chatId, String photoURL) {
        SendPhoto sendPhoto = new SendPhoto();
        InputFile inputFile = new InputFile(photoURL);
        sendPhoto.setChatId(String.valueOf(chatId));
        sendPhoto.setPhoto(inputFile);


        try {
            bot.execute(sendPhoto);
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
            bot.execute(sendPhotoRequest);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendVideoToOtherChanel(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        String fileId = update.getMessage().getVideo().getFileId();
        SendVideo sendVideoRequest = new SendVideo();
        sendVideoRequest.setChatId(chatId);
        sendVideoRequest.setVideo(new InputFile(fileId));
        try {
            bot.execute(sendVideoRequest);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void editMessageText(Long chatId, Integer messageId, String newTextForMessage, String newTextForButtonOne, String newTextForCallbackOne) {

        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(chatId);
        editMessage.setMessageId(messageId);
        editMessage.setText(newTextForMessage);
        editMessage.setReplyMarkup(getInlineKeyboard(newTextForButtonOne, newTextForCallbackOne));

        try {
            Message sentMessage = (Message) bot.execute(editMessage);
            lastMessageIds.put(chatId, sentMessage.getMessageId());

        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }

    private void editMessageTextCQ(Long chatId, Integer messageId, String newTextForMessage, String newTextForButtonOne, String newTextForCallbackOne) {

        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(chatId);
        editMessage.setMessageId(messageId);
        editMessage.setText(newTextForMessage);
        editMessage.setReplyMarkup(getInlineKeyboard(newTextForButtonOne, newTextForCallbackOne));

        try {
            Message sentMessage = (Message) bot.execute(editMessage);
            lastMessageIds.put(chatId, sentMessage.getMessageId());

        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }

    public void sendTypingStatus(Long chatId) {
        SendChatAction action = new SendChatAction();
        action.setChatId(chatId.toString());
        action.setAction(ActionType.valueOf(ActionType.TYPING.name()));
        try {
            bot.execute(action);  // Вызов метода execute из TelegramLongPollingBot
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void messageText(Long chatId, String newTextForMessage, String newTextForButtonOne, String newTextForCallbackOne) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        sendMessage.setText(newTextForMessage);
        sendMessage.setParseMode("HTML");
        sendMessage.setReplyMarkup(getInlineKeyboard(newTextForButtonOne, newTextForCallbackOne));
        try {
            Message sentMessage = bot.execute(sendMessage);
            lastMessageIds.put(chatId, sentMessage.getMessageId());
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }

    private void messageTextAndPhoto(Long chatId, String pathToPhoto, String newTextForMessage, String newTextForButtonOne, String newTextForCallbackOne) {

        SendPhoto sendMessage = new SendPhoto();
        sendMessage.setChatId(chatId);
        InputFile inputFile = new InputFile(pathToPhoto);
        sendMessage.setPhoto(inputFile);
        sendMessage.setCaption(newTextForMessage);
        sendMessage.setParseMode("HTML");
        sendMessage.setReplyMarkup(getInlineKeyboard(newTextForButtonOne, newTextForCallbackOne));
        try {
            Message sentMessage = bot.execute(sendMessage);
            lastMessageIds.put(chatId, sentMessage.getMessageId());
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }


    private void messageText(Long chatId, String newTextForMessage, String newTextForButtonOne, String newTextForButtonTwo, String newTextForCallbackOne, String newTextForCallbackTwo) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        sendMessage.setText(newTextForMessage);
        sendMessage.setParseMode("HTML");
        sendMessage.setReplyMarkup(getInlineKeyboard(newTextForButtonOne, newTextForButtonTwo, newTextForCallbackOne, newTextForCallbackTwo));
        try {
            Message sentMessage = bot.execute(sendMessage);
            lastMessageIds.put(chatId, sentMessage.getMessageId());
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }

    private void messageTextSchedule(Long chatId, String newTextForMessage, Client client) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        sendMessage.setText(newTextForMessage);
        sendMessage.setParseMode("HTML");

        try {
            Message sentMessage = bot.execute(sendMessage);
            lastMessageIds.put(chatId, sentMessage.getMessageId());


            client.listMessagesForDelete.add(sentMessage.getMessageId());
            listMessagesForDelete.put(client.getIdClient(), client.listMessagesForDelete);
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }

    private void editeMessage(Long chatId, int messageId, String newTextForMessage, String newTextForButtonOne, String newTextForCallbackOne) {

        EditMessageText sendMessage = new EditMessageText();
        sendMessage.setChatId(chatId);
        sendMessage.setMessageId(messageId);
        sendMessage.setText(newTextForMessage);
        sendMessage.setParseMode("HTML");
        sendMessage.setReplyMarkup(getInlineKeyboard(newTextForButtonOne, newTextForCallbackOne));
        try {
            EditMessageText sentMessage = (EditMessageText) bot.execute(sendMessage);
            lastMessageIds.put(chatId, sentMessage.getMessageId());
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }

    private InlineKeyboardMarkup getInlineKeyboard(String newTextForButtonOne, String newTextForCallbackOne) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();


        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(newTextForButtonOne);
        inlineKeyboardButton.setCallbackData(String.valueOf(newTextForCallbackOne));
        row.add(inlineKeyboardButton);


        keyboard.add(row);


        markup.setKeyboard(keyboard);
        return markup;
    }

    private void deleteMessageText(Long chatId, int messageId) {

        DeleteMessage sendMessage = new DeleteMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setMessageId(messageId);


        try {
            bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }

    private void editeMessage(Long chatId, int messageId, String newTextForMessage, String newTextForButtonOne, String newTextForButtonTwo, String newTextForButtonThree, String newTextForButtonFour, String newTextForButtonFive, String newTextForButtonSix, String newTextForButtonEight, String newTextForButtonNine, String newTextForButtonTen, String newTextForButtonEleven, String newTextForButtonTwelve, String newTextForButtonThirteen,
                              String newTextForCallbackOne, String newTextForCallbackTwo, String newTextForCallbackThree, String newTextForCallbackFour, String newTextForCallbackFive, String newTextForCallbackSix, String newTextForCallbackEight, String newTextForCallbackNine, String newTextForCallbackTen, String newTextForCallbackEleven, String newTextForCallbackTwelve, String newTextForCallbackThirteen) {

        EditMessageText message = new EditMessageText();
        message.setChatId(chatId);
        message.setText(newTextForMessage);
        message.setMessageId(messageId);
        message.setReplyMarkup(getInlineKeyboard(newTextForButtonOne, newTextForButtonTwo, newTextForButtonThree, newTextForButtonFour, newTextForButtonFive, newTextForButtonSix, newTextForButtonEight, newTextForButtonNine, newTextForButtonTen, newTextForButtonEleven, newTextForButtonTwelve, newTextForButtonThirteen,
                newTextForCallbackOne, newTextForCallbackTwo, newTextForCallbackThree, newTextForCallbackFour, newTextForCallbackFive, newTextForCallbackSix, newTextForCallbackEight, newTextForCallbackNine, newTextForCallbackTen, newTextForCallbackEleven, newTextForCallbackTwelve, newTextForCallbackThirteen));
        try {
            Message sentMessage = (Message) bot.execute(message);
            lastMessageIds.put(chatId, sentMessage.getMessageId());
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }


    private void editeMessage(Long chatId, int messageId, String newTextForMessage, String newTextForButtonOne, String newTextForButtonTwo, String newTextForButtonThree,
                              String newTextForCallbackOne, String newTextForCallbackTwo, String newTextForCallbackThree) {

        EditMessageText message = new EditMessageText();
        message.setChatId(chatId);
        message.setText(newTextForMessage);
        message.setMessageId(messageId);
        message.setReplyMarkup(getInlineKeyboard(newTextForButtonOne, newTextForButtonTwo, newTextForButtonThree,
                newTextForCallbackOne, newTextForCallbackTwo, newTextForCallbackThree));
        try {
            Message sentMessage = (Message) bot.execute(message);
            lastMessageIds.put(chatId, sentMessage.getMessageId());
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }

    private void messageText(Long chatId, String newTextForMessage, String newTextForButtonOne, String newTextForButtonTwo, String newTextForButtonThree,
                             String newTextForCallbackOne, String newTextForCallbackTwo, String newTextForCallbackThree) {

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(newTextForMessage);

        message.setReplyMarkup(getInlineKeyboard(newTextForButtonOne, newTextForButtonTwo, newTextForButtonThree,
                newTextForCallbackOne, newTextForCallbackTwo, newTextForCallbackThree));
        try {
            Message sentMessage = bot.execute(message);
            lastMessageIds.put(chatId, sentMessage.getMessageId());
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }

    private void messageText(Long chatId, String newTextForMessage, String newTextForButtonOne, String newTextForButtonTwo, String newTextForButtonThree, String newTextForButtonFour, String newTextForButtonFive, String newTextForButtonSix, String newTextForButtonSeven,
                             String newTextForCallbackOne, String newTextForCallbackTwo, String newTextForCallbackThree, String newTextForCallbackFour, String newTextForCallbackFive, String newTextForCallbackSix, String newTextForCallbackSeven) {

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(newTextForMessage);
        message.setReplyMarkup(getInlineKeyboard(newTextForButtonOne, newTextForButtonTwo, newTextForButtonThree, newTextForButtonFour, newTextForButtonFive, newTextForButtonSix, newTextForButtonSeven,
                newTextForCallbackOne, newTextForCallbackTwo, newTextForCallbackThree, newTextForCallbackFour, newTextForCallbackFive, newTextForCallbackSix, newTextForCallbackSeven));
        try {
            Message sentMessage = bot.execute(message);
            lastMessageIds.put(chatId, sentMessage.getMessageId());
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }

    private InlineKeyboardMarkup getInlineKeyboard(String newTextForButtonOne, String newTextForButtonTwo, String newTextForButtonThree, String newTextForButtonFour, String newTextForButtonFive, String newTextForButtonSix, String newTextForButtonSeven,
                                                   String newTextForCallbackOne, String newTextForCallbackTwo, String newTextForCallbackThree, String newTextForCallbackFour, String newTextForCallbackFive, String newTextForCallbackSix, String newTextForCallbackSeven) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        List<InlineKeyboardButton> row4 = new ArrayList<>();
        List<InlineKeyboardButton> row5 = new ArrayList<>();
        List<InlineKeyboardButton> row6 = new ArrayList<>();


        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(newTextForButtonOne);
        inlineKeyboardButton.setCallbackData(String.valueOf(newTextForCallbackOne));
        row.add(inlineKeyboardButton);

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText(newTextForButtonTwo);
        inlineKeyboardButton1.setCallbackData(String.valueOf(newTextForCallbackTwo));
        row1.add(inlineKeyboardButton1);

        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton2.setText(newTextForButtonThree);
        inlineKeyboardButton2.setCallbackData(String.valueOf(newTextForCallbackThree));
        row2.add(inlineKeyboardButton2);

        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
        inlineKeyboardButton3.setText(newTextForButtonFour);
        inlineKeyboardButton3.setCallbackData(String.valueOf(newTextForCallbackFour));
        row3.add(inlineKeyboardButton3);

        InlineKeyboardButton inlineKeyboardButton4 = new InlineKeyboardButton();
        inlineKeyboardButton4.setText(newTextForButtonFive);
        inlineKeyboardButton4.setCallbackData(String.valueOf(newTextForCallbackFive));
        row4.add(inlineKeyboardButton4);

        InlineKeyboardButton inlineKeyboardButton5 = new InlineKeyboardButton();
        inlineKeyboardButton5.setText(newTextForButtonSix);
        inlineKeyboardButton5.setCallbackData(String.valueOf(newTextForCallbackSix));
        row5.add(inlineKeyboardButton5);

        InlineKeyboardButton inlineKeyboardButton6 = new InlineKeyboardButton();
        inlineKeyboardButton6.setText(newTextForButtonSeven);
        inlineKeyboardButton6.setCallbackData(String.valueOf(newTextForCallbackSeven));
        row6.add(inlineKeyboardButton6);


        keyboard.add(row);
        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboard.add(row4);
        keyboard.add(row5);
        keyboard.add(row6);


        markup.setKeyboard(keyboard);
        return markup;
    }

    private void messageText(Long chatId, String newTextForMessage, String newTextForButtonOne, String newTextForButtonTwo, String newTextForButtonThree, String newTextForButtonFour, String newTextForButtonFive, String newTextForButtonSix, String newTextForButtonEight, String newTextForButtonNine, String newTextForButtonTen, String newTextForButtonEleven, String newTextForButtonTwelve, String newTextForButtonThirteen,
                             String newTextForCallbackOne, String newTextForCallbackTwo, String newTextForCallbackThree, String newTextForCallbackFour, String newTextForCallbackFive, String newTextForCallbackSix, String newTextForCallbackEight, String newTextForCallbackNine, String newTextForCallbackTen, String newTextForCallbackEleven, String newTextForCallbackTwelve, String newTextForCallbackThirteen) {

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(newTextForMessage);
        message.setReplyMarkup(getInlineKeyboard(newTextForButtonOne, newTextForButtonTwo, newTextForButtonThree, newTextForButtonFour, newTextForButtonFive, newTextForButtonSix, newTextForButtonEight, newTextForButtonNine, newTextForButtonTen, newTextForButtonEleven, newTextForButtonTwelve, newTextForButtonThirteen,
                newTextForCallbackOne, newTextForCallbackTwo, newTextForCallbackThree, newTextForCallbackFour, newTextForCallbackFive, newTextForCallbackSix, newTextForCallbackEight, newTextForCallbackNine, newTextForCallbackTen, newTextForCallbackEleven, newTextForCallbackTwelve, newTextForCallbackThirteen));
        try {
            Message sentMessage = bot.execute(message);
            adminPanelMessageIdsToAdmin.put(chatId, sentMessage.getMessageId());
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }

    private InlineKeyboardMarkup getInlineKeyboard(String newTextForButtonOne, String newTextForButtonTwo, String newTextForButtonThree, String newTextForButtonFour, String newTextForButtonFive, String newTextForButtonSix, String newTextForButtonEight, String newTextForButtonNine, String newTextForButtonTen, String newTextForButtonEleven, String newTextForButtonTwelve, String newTextForButtonThirteen,
                                                   String newTextForCallbackOne, String newTextForCallbackTwo, String newTextForCallbackThree, String newTextForCallbackFour, String newTextForCallbackFive, String newTextForCallbackSix, String newTextForCallbackEight, String newTextForCallbackNine, String newTextForCallbackTen, String newTextForCallbackEleven, String newTextForCallbackTwelve, String newTextForCallbackThirteen) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        List<InlineKeyboardButton> row4 = new ArrayList<>();
        List<InlineKeyboardButton> row5 = new ArrayList<>();
        List<InlineKeyboardButton> row7 = new ArrayList<>();
        List<InlineKeyboardButton> row8 = new ArrayList<>();
        List<InlineKeyboardButton> row9 = new ArrayList<>();
        List<InlineKeyboardButton> row10 = new ArrayList<>();
        List<InlineKeyboardButton> row11 = new ArrayList<>();
        List<InlineKeyboardButton> row12 = new ArrayList<>();


        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(newTextForButtonOne);
        inlineKeyboardButton.setCallbackData(String.valueOf(newTextForCallbackOne));
        row.add(inlineKeyboardButton);

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText(newTextForButtonTwo);
        inlineKeyboardButton1.setCallbackData(String.valueOf(newTextForCallbackTwo));
        row1.add(inlineKeyboardButton1);

        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton2.setText(newTextForButtonThree);
        inlineKeyboardButton2.setCallbackData(String.valueOf(newTextForCallbackThree));
        row2.add(inlineKeyboardButton2);

        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
        inlineKeyboardButton3.setText(newTextForButtonFour);
        inlineKeyboardButton3.setCallbackData(String.valueOf(newTextForCallbackFour));
        row3.add(inlineKeyboardButton3);

        InlineKeyboardButton inlineKeyboardButton4 = new InlineKeyboardButton();
        inlineKeyboardButton4.setText(newTextForButtonFive);
        inlineKeyboardButton4.setCallbackData(String.valueOf(newTextForCallbackFive));
        row4.add(inlineKeyboardButton4);

        InlineKeyboardButton inlineKeyboardButton5 = new InlineKeyboardButton();
        inlineKeyboardButton5.setText(newTextForButtonSix);
        inlineKeyboardButton5.setCallbackData(String.valueOf(newTextForCallbackSix));
        row5.add(inlineKeyboardButton5);


        InlineKeyboardButton inlineKeyboardButton7 = new InlineKeyboardButton();
        inlineKeyboardButton7.setText(newTextForButtonEight);
        inlineKeyboardButton7.setCallbackData(String.valueOf(newTextForCallbackEight));
        row7.add(inlineKeyboardButton7);

        InlineKeyboardButton inlineKeyboardButton8 = new InlineKeyboardButton();
        inlineKeyboardButton8.setText(newTextForButtonNine);
        inlineKeyboardButton8.setCallbackData(String.valueOf(newTextForCallbackNine));
        row8.add(inlineKeyboardButton8);

        InlineKeyboardButton inlineKeyboardButton9 = new InlineKeyboardButton();
        inlineKeyboardButton9.setText(newTextForButtonTen);
        inlineKeyboardButton9.setCallbackData(String.valueOf(newTextForCallbackTen));
        row9.add(inlineKeyboardButton9);

        InlineKeyboardButton inlineKeyboardButton10 = new InlineKeyboardButton();
        inlineKeyboardButton10.setText(newTextForButtonEleven);
        inlineKeyboardButton10.setCallbackData(String.valueOf(newTextForCallbackEleven));
        row10.add(inlineKeyboardButton10);

        InlineKeyboardButton inlineKeyboardButton11 = new InlineKeyboardButton();
        inlineKeyboardButton11.setText(newTextForButtonTwelve);
        inlineKeyboardButton11.setCallbackData(String.valueOf(newTextForCallbackTwelve));
        row11.add(inlineKeyboardButton11);

        InlineKeyboardButton inlineKeyboardButton12 = new InlineKeyboardButton();
        inlineKeyboardButton12.setText(newTextForButtonThirteen);
        inlineKeyboardButton12.setCallbackData(String.valueOf(newTextForCallbackThirteen));
        row12.add(inlineKeyboardButton12);


        keyboard.add(row);
        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboard.add(row4);
        keyboard.add(row5);
        keyboard.add(row7);
        keyboard.add(row8);
        keyboard.add(row9);
        keyboard.add(row10);
        keyboard.add(row11);
        keyboard.add(row12);


        markup.setKeyboard(keyboard);
        return markup;
    }

    private InlineKeyboardMarkup getInlineKeyboard(String newTextForButtonOne, String newTextForButtonTwo, String newTextForButtonThree,
                                                   String newTextForCallbackOne, String newTextForCallbackTwo, String newTextForCallbackThree) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();


        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(newTextForButtonOne);
        inlineKeyboardButton.setCallbackData(String.valueOf(newTextForCallbackOne));
        row.add(inlineKeyboardButton);

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText(newTextForButtonTwo);
        inlineKeyboardButton1.setCallbackData(String.valueOf(newTextForCallbackTwo));
        row.add(inlineKeyboardButton1);

        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton2.setText(newTextForButtonThree);
        inlineKeyboardButton2.setCallbackData(String.valueOf(newTextForCallbackThree));
        row1.add(inlineKeyboardButton2);


        keyboard.add(row);
        keyboard.add(row1);


        markup.setKeyboard(keyboard);
        return markup;
    }

    public void sendPhoto(Client client, String str, String url) {

        SendPhoto sendPhoto = new SendPhoto();
        InputFile inputFile = new InputFile(url);
        sendPhoto.setCaption(str);

        sendPhoto.setChatId(client.getIdClient());
        sendPhoto.setPhoto(inputFile);


        try {
            Message sentMessage = bot.execute(sendPhoto);
            lastMessageIdsPhoto.put(client.getIdClient(), sentMessage.getMessageId());
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }

    }

    private void messageTextStory(Long chatId, String newTextForMessage, String newTextForButtonOne, String newTextForButtonTwo, String newTextForCallbackOne, String newTextForCallbackTwo) {
        long idclient;

        if (newTextForCallbackTwo.contains("doOrder")) {
            idclient = Long.parseLong(newTextForCallbackTwo.replaceAll("doOrder", "").trim());
        } else {
            idclient = Long.parseLong(newTextForCallbackTwo.replaceAll("cancelCons", "").trim());
        }
        System.out.println(idclient);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(newTextForMessage);
        message.setReplyMarkup(getInlineKeyboard(newTextForButtonOne, newTextForButtonTwo, newTextForCallbackOne, newTextForCallbackTwo));
        try {

            Message sentMessage = bot.execute(message);
            lastMessageIdsToAdmin.put(chatId + idclient, sentMessage.getMessageId());
            System.out.println(chatId + idclient);
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }

    private InlineKeyboardMarkup getInlineKeyboard(String newTextForButtonOne, String newTextForButtonTwo, String newTextForCallbackOne, String newTextForCallbackTwo) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();


        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(newTextForButtonOne);
        inlineKeyboardButton.setCallbackData(String.valueOf(newTextForCallbackOne));
        row.add(inlineKeyboardButton);

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText(newTextForButtonTwo);
        inlineKeyboardButton1.setCallbackData(String.valueOf(newTextForCallbackTwo));
        row.add(inlineKeyboardButton1);


        keyboard.add(row);


        markup.setKeyboard(keyboard);
        return markup;
    }


    private void messageText(Long chatId, String newTextForMessage) {

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setParseMode("HTML");

        message.setText(newTextForMessage);

        try {
            Message sentMessage = bot.execute(message);
            lastMessageIds.put(chatId, sentMessage.getMessageId());
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }

    private void editMessageText(Long chatId, String newTextForMessage, String newTextForButtonOne, String newTextForButtonTwo, String newTextForCallbackOne, String newTextForCallbackTwo) {
        long idclient;
        if (newTextForCallbackTwo.contains("doOrder")) {
            idclient = Long.parseLong(newTextForCallbackTwo.replaceAll("doOrder", "").trim());
        } else {
            idclient = Long.parseLong(newTextForCallbackTwo.replaceAll("cancelCons", "").trim());
        }
        System.out.println(idclient);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(newTextForMessage);
        message.setReplyMarkup(getInlineKeyboard(newTextForButtonOne, newTextForButtonTwo, newTextForCallbackOne, newTextForCallbackTwo));
        try {
            Message sentMessage = bot.execute(message);
            lastMessageIdsToAdmin.put(chatId + idclient, sentMessage.getMessageId());
            System.out.println(chatId + idclient);
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }

    private void sendDocument(Update update, long chatId) {
        Document document = update.getMessage().getDocument();

        // Получаем файл из объекта документа
        InputFile inputFile = new InputFile(document.getFileId());

        // Создаем объект для отправки документа обратно пользователю
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(chatId);
        sendDocument.setDocument(inputFile);

        try {
            // Отправляем документ обратно пользователю
            bot.execute(sendDocument);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


}
