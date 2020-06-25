package com.example.buysellrent.Class;

import android.graphics.Bitmap;
import android.net.Uri;

import java.util.ArrayList;

public class AdvertisementCarModel {

    private String SellerId;
    private String Brand;
    private int Year ;
    private int Driven ;
    private String transmission ;
    private String Title;
    private String Desc;
    private String Fuel;
    private long price;
    private boolean accepted;
    private Uri image;

    //private ArrayList<Uri> imageList;

    public AdvertisementCarModel(String brand, int year, int driven, String transmission, String title, String desc, String fuel, long price) {
        Brand = brand;
        Year = year;
        Driven = driven;
        this.transmission = transmission;
        Title = title;
        Desc = desc;
        Fuel = fuel;
        accepted=false;

        this.price=price;
        //this.image=image;
        //this.imageList=imageList;
    }

//    public void setImageList(ArrayList<Uri> imageList) {
//        this.imageList = imageList;
//    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
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
