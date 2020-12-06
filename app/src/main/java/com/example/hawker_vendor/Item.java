package com.example.hawker_vendor;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class Item implements Parcelable {
    private String id, name, imagePath, hawkerId;
    private float price;
    private int dailyStock, currentStock;

    protected Item(Parcel in) {
        id = in.readString();
        name = in.readString();
        imagePath = in.readString();
        hawkerId = in.readString();
        price = in.readFloat();
        dailyStock = in.readInt();
        currentStock = in.readInt();
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    public Item() {}

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

    public String getHawkerId() {
        return hawkerId;
    }

    public void setHawkerId(String hawkerId) {
        this.hawkerId = hawkerId;
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

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();

        map.put("name", name);
        map.put("price", price);
        map.put("imagePath", imagePath);
        map.put("hawkerId", hawkerId);
        map.put("dailyStock", dailyStock);
        map.put("currentStock", currentStock);

        return map;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", hawkerId='" + hawkerId + '\'' +
                ", price=" + price +
                ", dailyStock=" + dailyStock +
                ", currentStock=" + currentStock +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(imagePath);
        parcel.writeString(hawkerId);
        parcel.writeFloat(price);
        parcel.writeInt(dailyStock);
        parcel.writeInt(currentStock);
    }
}
