package com.sma.proiect;

public class ItemForMenu {

    private String text;
    private int imageID;

    public ItemForMenu(String text, int imageID) {
        this.text = text;
        this.imageID = imageID;
    }

    public String getText() {
        return this.text;
    }

    public int getImageID() {
        return this.imageID;
    }
}
