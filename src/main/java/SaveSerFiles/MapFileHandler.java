package SaveSerFiles;

import com.xakerz.SalerBots.Client;
import com.xakerz.SalerBots.WebHookController;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;

import static com.xakerz.SalerBots.WebHookController.*;


public class MapFileHandler {
static String pathToFile = "/root/SalerBots/SalerBots/maps.ser";

    public static void saveMaps() {
        try {
            FileOutputStream fileOut = new FileOutputStream(pathToFile);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(lastMessageIds);
            out.writeObject(lastMessageIdsToAdmin);
            out.writeObject(adminPanelMessageIdsToAdmin);
            out.writeObject(lastMessageIdsPhoto);
            out.writeObject(lastMessageIdsMessagePay);
            out.writeObject(listMessagesConsult);
            out.writeObject(clients);
            out.writeObject(listClientMessages);
            out.close();
            fileOut.close();
            System.out.println("Карты успешно сохранены.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void loadMaps() {
        try {
            FileInputStream fileIn = new FileInputStream(pathToFile);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            lastMessageIds = (Map<Long, Integer>) in.readObject();
            lastMessageIdsToAdmin = (Map<Long, Integer>) in.readObject();
            adminPanelMessageIdsToAdmin = (Map<Long, Integer>) in.readObject();
            lastMessageIdsPhoto = (Map<Long, Integer>) in.readObject();
            lastMessageIdsMessagePay = (Map<Long, Integer>) in.readObject();
            listMessagesConsult = (Map<Long, Integer>) in.readObject();
            clients = (Map<Long, Client>) in.readObject();
            listClientMessages = (Map<Long, ArrayList<String>>) in.readObject();
            in.close();
            fileIn.close();
            System.out.println("Карты успешно загружены.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
