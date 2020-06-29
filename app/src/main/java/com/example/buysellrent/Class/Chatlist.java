package com.example.buysellrent.Class;

public class Chatlist {

    public String id, last;

    public Chatlist() {
    }

    public Chatlist(String id, String last) {
        this.id = id;
        this.last = last;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }
}
