package com.example.buysellrent.Class;

import com.example.buysellrent.Class.User;

public class ChatBox {

    private String sender;
    private String receiver;
    private String message;
    private String type;
    private boolean isseen;

    public ChatBox(String sender, String receiver, String message,boolean isseen, String type) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.type = type;
        this.isseen=isseen;
    }
    public ChatBox(){

    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
