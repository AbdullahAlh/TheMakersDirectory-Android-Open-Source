package com.themakersdirectory.entity;

import com.themakersdirectory.AppManager;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by xlethal on 8/8/16.
 */

public class Branch implements Serializable {
    public double lat;
    public double lng;
    public String name;
    public String name_ar;
    public ArrayList<Object> phone_number;

    public Branch() {

    }

    public Branch(double lat, double lng, String name, String name_ar) {
        this.lat = lat;
        this.lng = lng;
        this.name = name;
        this.name_ar = name_ar;
    }

    public String name() {
        if (AppManager.isArabic())
            return name_ar;
        else
            return name;
    }

    public void setPhoneNumber(ArrayList<Object> phone_number) {
        this.phone_number = phone_number;
    }

}
