package com.swaraj.myapplication.data;

public class ProductData {
    String name,barCode, imagePath, videoPath;
    int wPrice, rPrice,quantity;

    public ProductData() {
    }

    public ProductData(String name, String barCode, String imagePath, int wPrice, int rPrice, int quantity, String videoPath) {
        this.name = name;
        this.barCode = barCode;
        this.imagePath = imagePath;
        this.wPrice = wPrice;
        this.rPrice = rPrice;
        this.quantity = quantity;
        this.videoPath = videoPath;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getwPrice() {
        return wPrice;
    }

    public void setwPrice(int wPrice) {
        this.wPrice = wPrice;
    }

    public int getrPrice() {
        return rPrice;
    }

    public void setrPrice(int rPrice) {
        this.rPrice = rPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
