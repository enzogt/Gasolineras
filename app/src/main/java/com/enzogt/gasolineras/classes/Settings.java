package com.enzogt.gasolineras.classes;

public class Settings {

    public static final int THEME_AUTO   = 0;
    public static final int THEME_BRIGHT = 1;
    public static final int THEME_DARK   = 2;

    private int theme = THEME_AUTO;

    private int idProduct = -1;
    private String nameProduct = "";

    private int idLocation = -1;
    private String nameLocation = "";

    public Settings() {

    }

    public int getTheme() {
        return theme;
    }

    public void setTheme(int theme) {
        this.theme = theme;
    }

    public int getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }

    public String getNameProduct() {
        return nameProduct;
    }

    public void setNameProduct(String nameProduct) {
        this.nameProduct = nameProduct;
    }

    public int getIdLocation() {
        return idLocation;
    }

    public void setIdLocation(int idLocation) {
        this.idLocation = idLocation;
    }

    public String getNameLocation() {
        return nameLocation;
    }

    public void setNameLocation(String nameLocation) {
        this.nameLocation = nameLocation;
    }
}
