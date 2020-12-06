package com.example.hawker_vendor;

public class Hawker {

    private static Hawker instance;

    private String id, name;
    private boolean isOpen;

    private Hawker(String id, String name, boolean isOpen) {
        this.id = id;
        this.name = name;
        this.isOpen = isOpen;
    }

    public static Hawker getInstance() {
        if (instance == null) {

        }

        return instance;
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

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }
}
