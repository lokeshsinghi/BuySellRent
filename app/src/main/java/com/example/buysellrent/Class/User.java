package com.example.buysellrent.Class;

public class User {
    private  String fullName, email, imageUrl, id;
    private boolean verifiedEmail, default_dp;

    public User(String fullName, String email, String id) {
        this.fullName = fullName;
        this.email = email;
        this.id = id;
        this.imageUrl = "";
        this.default_dp = true;
        this.verifiedEmail = false;
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

    public boolean isVerifiedEmail() {
        return verifiedEmail;
    }

    public void setVerifiedEmail(boolean verifiedEmail) {
        this.verifiedEmail = verifiedEmail;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public boolean isDefault_dp() { return default_dp; }

    public void setDefault_dp(boolean default_dp) { this.default_dp = default_dp; }
}
