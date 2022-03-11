package com.enzogt.gasolineras.classes;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Products {

    public static final String URL_API = "https://sedeaplicaciones.minetur.gob.es/ServiciosRESTCarburantes/PreciosCarburantes/Listados/ProductosPetroliferos/";

    private ArrayList<Product> list;

    public Products(String responseApi) {

        this.list = new ArrayList<>();

        try {
            JSONArray array = new JSONArray(responseApi);

            int id;
            String name;

            for (int i = 0; i < array.length(); i++) {
                JSONObject row = array.getJSONObject(i);
                id = Integer.parseInt(row.getString("IDProducto"));
                name = row.getString("NombreProducto");
                this.list.add(new Product(id, name));
            }

        } catch (Exception e) {
            e.printStackTrace();
            this.list = null;
        }
    }

    public ArrayList<Product> getList() {
        return list;
    }

    public int getIdProductByName(String name) {
        for (Product product : list)
            if (product.getName().equals(name))
                return product.getId();
        return -1;
    }

    public static class Product {

        private final int id;
        private final String name;

        public Product(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

    }

}

/*
    https://sedeaplicaciones.minetur.gob.es/ServiciosRESTCarburantes/PreciosCarburantes/Listados/ProductosPetroliferos/

    [{
        "IDProducto": "1",
        "NombreProducto": "Gasolina 95 E5",
        "NombreProductoAbreviatura": "G95E5"
    }, {
        ...
    }]
*/