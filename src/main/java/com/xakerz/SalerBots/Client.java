package com.xakerz.SalerBots;

import java.io.Serializable;
import java.util.ArrayList;

public class Client implements Serializable {
    private String name;
    private String nickName;
    private long idClient;
    private boolean questionToAdmin;

    private int messageId;

    private String situation;

    private boolean booleanSituation;
    long idToMessage;
   private boolean booleanInfo;
   private boolean nameInfo;
   private boolean ageInfo;
   private String info;
    private boolean saveMessages;
    private String nameForInfo;

    private int ageForInfo;
    ArrayList<String> clientMessages = new ArrayList<>();
    ArrayList<Integer> listMessagesForDelete = new ArrayList<>();

    public String getNameForInfo() {
        return nameForInfo;
    }

    public void setNameForInfo(String nameForInfo) {
        this.nameForInfo = nameForInfo;
    }

    public int getAgeForInfo() {
        return ageForInfo;
    }

    public void setAgeForInfo(int ageForInfo) {
        this.ageForInfo = ageForInfo;
    }

    public boolean isNameInfo() {
        return nameInfo;
    }

    public void setNameInfo(boolean nameInfo) {
        this.nameInfo = nameInfo;
    }

    public boolean isAgeInfo() {
        return ageInfo;
    }

    public void setAgeInfo(boolean ageInfo) {
        this.ageInfo = ageInfo;
    }

    public boolean isSaveMessages() {
        return saveMessages;
    }

    public void setSaveMessages(boolean saveMessages) {
        this.saveMessages = saveMessages;
    }

    public boolean isBooleanInfo() {
        return booleanInfo;
    }

    public void setBooleanInfo(boolean booleanInfo) {
        this.booleanInfo = booleanInfo;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }
    public boolean isBooleanSituation() {
        return booleanSituation;
    }

    public void setBooleanSituation(boolean booleanSituation) {
        this.booleanSituation = booleanSituation;
    }

    public long getIdToMessage() {
        return idToMessage;
    }

    public void setIdToMessage(long idToMessage) {
        this.idToMessage = idToMessage;
    }





    public Client() {
    }

    public Client(String name, String nickName, long idClient, boolean questionToAdmin) {
        this.name = name;
        this.nickName = "@" + nickName;
        this.idClient = idClient;
        this.questionToAdmin = questionToAdmin;
        idToMessage = 0;
        this.booleanSituation = false;
        this.situation = "";
        this.booleanInfo = false;
        this.info = "";
        this.saveMessages = false;
        this.nameInfo = false;
        this.ageInfo = false;
        this.ageForInfo = 0;
        this.nameForInfo = "Empty";
    }



    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }



    public long getIdClient() {
        return idClient;
    }

    public void setIdClient(long idClient) {
        this.idClient = idClient;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public boolean isQuestionToAdmin() {
        return questionToAdmin;
    }

    public void setQuestionToAdmin(boolean questionToAdmin) {
        this.questionToAdmin = questionToAdmin;
    }





}
