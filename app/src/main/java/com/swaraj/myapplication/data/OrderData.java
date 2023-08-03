package com.swaraj.myapplication.data;

public class OrderData {
    String name,barCode;
    int  rPrice,quantity;

    public OrderData() {
    }

    public OrderData(String name, String barCode, int rPrice, int quantity) {
        this.name = name;
        this.barCode = barCode;
        this.rPrice = rPrice;
        this.quantity = quantity;

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
