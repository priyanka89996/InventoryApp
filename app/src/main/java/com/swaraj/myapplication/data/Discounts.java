package com.swaraj.myapplication.data;

import java.util.Date;

public class Discounts {

    public String Type;
    public String Start_Date;
    public String End_Date;
    public String Data;
    public String ExtraData;
    public String Product_Barcode;


    public Discounts() {
    }

    public Discounts (String type, String data1, String data2, String proBar, String sd, String ed ) {
        this.Type = type;
        this.Data = data1;
        this.ExtraData = data2;
        this.Product_Barcode = proBar;
        this.Start_Date = sd;
        this.End_Date = ed;

    }


}
