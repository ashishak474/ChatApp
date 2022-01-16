package com.example.chatme.Model;

public class ChatModel {

    private String sender;
    private String recever;
    private String message;
   private boolean isSeen;

    public ChatModel(String sender, String recever, String message,boolean isSeen) {
        this.sender = sender;
        this.recever = recever;
        this.message = message;
        this.isSeen=isSeen;
    }

    public ChatModel() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecever() {
        return recever;
    }

    public void setRecever(String recever) {
        this.recever = recever;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }
}
