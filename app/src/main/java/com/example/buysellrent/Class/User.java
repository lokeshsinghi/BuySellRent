package com.example.buysellrent.Class;



public class User {
    private  String fullName, email, imageUrl, id,status;


    public User(String fullName, String email, String id) {
        this.fullName = fullName;
        this.email = email;
        this.id = id;
        this.imageUrl = "";
        this.status="online";

    }
    public User() {

    }
    public User(String fullName, String email, String id, String imageUrl) {
        this.fullName = fullName;
        this.email = email;
        this.id = id;
        this.imageUrl = imageUrl;
    }



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }


}
