package com.example.buysellrent.Class;

import com.example.buysellrent.ui.sell.AdvertisementModel;

public class AdvertisementCarModel extends AdvertisementModel {

    private String AdId;
    private String SellerId;
    private String Brand;
    private String category;
    private int Year ;
    private int Driven ;
    private String transmission;
    private String Title;
    private String Desc;
    private String Fuel;
    private String number;
    private String address;
    private String price;
    private boolean accepted;
    private int imgCount;



    public AdvertisementCarModel(String brand, int year, int driven, String transmission, String title, String desc, String fuel, String price, String category, String number, String address) {
        AdId="Unknown";
        SellerId="Unknown";
        Brand = brand;
        Year = year;
        Driven = driven;
        this.transmission = transmission;
        Title = title;
        Desc = desc;
        Fuel = fuel;
        accepted=false;
        this.price=price;
        this.category=category;
        imgCount=0;
        this.number=number;
        this.address = address;
    }

    public AdvertisementCarModel(){}

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAdId() {
        return AdId;
    }

    public void setAdId(String adId) {
        AdId = adId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
//    public void setImageList(ArrayList<Uri> imageList) {
//        this.imageList = imageList;
//    }


    public int getImgCount() {
        return imgCount;
    }

    public void setImgCount(int imgCount) {
        this.imgCount = imgCount;
    }


    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSellerId() {
        return SellerId;
    }

    public void setSellerId(String sellerId) {
        SellerId = sellerId;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public String getBrand() {
        return Brand;
    }

    public void setBrand(String brand) {
        Brand = brand;
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

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
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

    public String getFuel() {
        return Fuel;
    }

    public void setFuel(String fuel) {
        Fuel = fuel;
    }


}
