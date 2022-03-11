package com.enzogt.gasolineras.classes;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Locations {

    public static final String URL_API = "https://sedeaplicaciones.minetur.gob.es/ServiciosRESTCarburantes/PreciosCarburantes/Listados/Municipios/";

    private ArrayList<Location> list;

    public Locations(String responseApi) {

        this.list = new ArrayList<>();

        try {
            JSONArray array = new JSONArray(responseApi);

            int id;
            String name;

            for (int i = 0; i < array.length(); i++) {
                JSONObject row = array.getJSONObject(i);
                id = Integer.parseInt(row.getString("IDMunicipio"));
                name = row.getString("Municipio");
                this.list.add(new Location(id, name));
            }

        } catch (Exception e) {
            e.printStackTrace();
            this.list = null;
        }
    }

    public ArrayList<Location> getList() {
        return list;
    }

    public int getIdLocationByName(String name) {
        for (Location location : list)
            if (location.getName().equals(name))
                return location.getId();
        return -1;
    }

    public static class Location {

        private final int id;
        private final String name;

        public Location(int id, String name) {
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
    https://sedeaplicaciones.minetur.gob.es/ServiciosRESTCarburantes/PreciosCarburantes/Listados/Municipios/

    [{
        "IDMunicipio": "1",
        "IDProvincia": "01",
        "IDCCAA": "16",
        "Municipio": "Alegría-Dulantzi",
        "Provincia": "ARABA\/ÁLAVA",
        "CCAA": "País Vasco"
    }, {
        ...
    }]
*/