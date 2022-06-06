package com.example.fortest.model;

public class FoodModel {
    private String image,price,name,key;

    public FoodModel(String image, String price, String name, String key) {
        this.image = image;
        this.price = price;
        this.name = name;
        this.key = key;
    }

    public FoodModel(){

    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
