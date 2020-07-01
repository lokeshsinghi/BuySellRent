package com.example.buysellrent.ui.sell;

public class AdvertisementBikeModel extends AdvertisementModel {

    private String AdId;
    private String SellerId;
    private String Brand;
    private String category;
    private int Year ;
    private int Driven ;
    private String Title;
    private String Desc;
    private String number;
    private String address;
    private String price;
    private boolean accepted;
    private int imgCount;

    public AdvertisementBikeModel(String brand, int year, int driven, String title, String desc,String price, String category, String number, String address) {

        Brand = brand;
        this.category = category;
        Year = year;
        Driven = driven;
        Title = title;
        Desc = desc;
        this.number = number;
        this.address = address;
        this.price = price;
        this.accepted = false;
        this.imgCount = 0;
    }

    public String getAdId() {
        return AdId;
    }

    public void setAdId(String adId) {
        AdId = adId;
    }

    public String getSellerId() {
        return SellerId;
    }

    public void setSellerId(String sellerId) {
        SellerId = sellerId;
    }

    public String getBrand() {
        return Brand;
    }

    public void setBrand(String brand) {
        Brand = brand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getYear() {
        return Year;
    }

    public void setYear(int year) {
        Year = year;
    }

    public int getDriven() {
        return Driven;
    }

    public void setDriven(int driven) {
        Driven = driven;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public int getImgCount() {
        return imgCount;
    }

    public void setImgCount(int imgCount) {
        this.imgCount = imgCount;
    }
}
