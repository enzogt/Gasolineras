package com.enzogt.gasolineras.util;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.enzogt.gasolineras.classes.Locations;
import com.enzogt.gasolineras.classes.Products;
import com.enzogt.gasolineras.classes.SearchResults;
import com.enzogt.gasolineras.classes.Settings;
import com.google.gson.Gson;

public class MySharedPreferences {

    public static final String SP_NAME  = "MySharedPreferences";

    public static final String KEY_SETTINGS  = "settings";
    public static final String KEY_PRODUCTS  = "products";
    public static final String KEY_LOCATIONS = "locations";
    public static final String KEY_SEARCH    = "search_results";


    private static Object readObject (Context context, String key, Class<?> classObject) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        String json = sp.getString(key, null);
        return (json != null ? new Gson().fromJson(json, classObject) : null);
    }

    private static void saveObject (Context context, String key, Object Object) {
        SharedPreferences  sp = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        SharedPreferences.Editor spe = sp.edit();
        spe.putString(key, new Gson().toJson(Object));
        spe.apply();
    }

    // Read

    public static Settings readSettings (Context context) {
        return (Settings) readObject(context, KEY_SETTINGS, Settings.class);
    }

    public static Products readProducts (Context context) {
        return (Products) readObject(context, KEY_PRODUCTS, Products.class);
    }

    public static Locations readLocations (Context context) {
        return (Locations) readObject(context, KEY_LOCATIONS, Locations.class);
    }

    public static SearchResults readSearchResults (Context context) {
        return (SearchResults) readObject(context, KEY_SEARCH, SearchResults.class);
    }

    // Save

    public static void saveSettings (Context context, Settings settings) {
        saveObject(context, KEY_SETTINGS, settings);
    }

    public static void saveProducts (Context context, Products products) {
        saveObject(context, KEY_PRODUCTS, products);
    }

    public static void saveLocations (Context context, Locations locations) {
        saveObject(context, KEY_LOCATIONS, locations);
    }

    public static void saveSearchResults (Context context, SearchResults searchResults) {
        saveObject(context, KEY_SEARCH, searchResults);
    }
}