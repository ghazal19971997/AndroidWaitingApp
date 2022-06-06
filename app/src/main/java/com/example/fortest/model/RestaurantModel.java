package com.example.fortest.model;

import java.util.List;

public class RestaurantModel {
    private String key;
    private String address;
    private String grade;
    private String image;
    private String name;
    private String open_hours;
    private String tell;
    private String waiting_situation;

    public RestaurantModel() {
    }

    public RestaurantModel(String address, String grade, String image,String name, String open_hours, String tell, String waiting_situation) {
        this.address = address;
        this.grade = grade;
        this.image = image;
        this.name = name;
        this.open_hours = open_hours;
        this.tell = tell;
        this.waiting_situation = waiting_situation;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpen_hours() {
        return open_hours;
    }

    public void setOpen_hours(String open_hours) {
        this.open_hours = open_hours;
    }

    public String getTell() {
        return tell;
    }

    public void setTell(String tell) {
        this.tell = tell;
    }

    public String getWaiting_situation() {
        return waiting_situation;
    }

    public void setWaiting_situation(String waiting_situation) {
        this.waiting_situation = waiting_situation;
    }
}