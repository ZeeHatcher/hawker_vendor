package com.example.hawker_vendor;

import android.content.Intent;

import com.google.firebase.firestore.DocumentSnapshot;

public class Item {
    private String id, name, imagePath;
    private float price;
    private int dailyStock, currentStock;

    public static Item fromDocument(DocumentSnapshot doc) {
        return new Item(
                doc.getId(),
                doc.get("name", String.class),
                doc.get("imagePath", String.class),
                doc.get("price", Float.class),
                doc.get("dailyStock", Integer.class),
                doc.get("currentStock", Integer.class)
        );
    }

    public Item() {}

    public Item(String id, String name, String imagePath, float price, int dailyStock, int currentStock) {
        this.id = id;
        this.name = name;
        this.imagePath = imagePath;
        this.price = price;
        this.dailyStock = dailyStock;
        this.currentStock = currentStock;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getDailyStock() {
        return dailyStock;
    }

    public void setDailyStock(int dailyStock) {
        this.dailyStock = dailyStock;
    }

    public int getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", price=" + price +
                ", dailyStock=" + dailyStock +
                ", currentStock=" + currentStock +
                '}';
    }
}
