package com.enzogt.gasolineras.classes;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class SearchResults {

    public static final String URL_API = "https://sedeaplicaciones.minetur.gob.es/ServiciosRESTCarburantes/PreciosCarburantes/EstacionesTerrestres/FiltroMunicipioProducto/{idLocation}/{idProduct}";

    private Date date;
    private String productName;
    private String locationName;
    private ArrayList<GasStation> gasStations;

    public SearchResults(String responseApi, String nameProduct, String nameLocation) {

        this.gasStations = new ArrayList<>();

        try {
            JSONObject root = new JSONObject(responseApi);
            JSONArray list = root.getJSONArray("ListaEESSPrecio");

            String name, address;
            float price;

            for (int i = 0; i < list.length(); i++) {
                JSONObject row = list.getJSONObject(i);

                name = row.getString("Rótulo");
                address = row.getString("Dirección");
                price = Float.parseFloat(row.getString("PrecioProducto").replace(",", "."));

                this.gasStations.add(new GasStation(name, address, price));
            }

            Collections.sort(this.gasStations);
            this.date = new Date();
            this.productName = nameProduct;
            this.locationName = nameLocation;

        } catch (Exception e) {
            e.printStackTrace();
            this.gasStations = null;
        }
    }

    public Date getDate() {
        return date;
    }

    public String getProductName() {
        return productName;
    }

    public String getLocationName() {
        return locationName;
    }

    public ArrayList<GasStation> getGasStations() {
        return gasStations;
    }

    public static class GasStation implements Comparable<GasStation> {

        private final String name;
        private final String street;
        private final float price;

        public GasStation(String name, String street, float price) {
            this.name = name;
            this.street = street;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public String getStreet() {
            return street;
        }

        public float getPrice() {
            return price;
        }

        @Override
        public int compareTo(GasStation gasStation) {
            return Math.round((this.price - gasStation.price)*1000);
        }

        @NonNull
        @Override
        public String toString() {
            return "GasStation{" +
                    "name='" + name + '\'' +
                    ", street='" + street + '\'' +
                    ", price=" + price +
                    '}';
        }
    }

}

/*
    // https://sedeaplicaciones.minetur.gob.es/ServiciosRESTCarburantes/PreciosCarburantes/EstacionesTerrestres/FiltroMunicipioProducto/3394/1

    {
        "Fecha": "17\/11\/2021 20:10:45",
        "ListaEESSPrecio": [{
            "C.P.": "22003",
            "Dirección": "CALLE COSO ALTO, 65",
            "Horario": "L-V: 07:00-21:00; S: 08:00-14:00",
            "Latitud": "42,141194",
            "Localidad": "HUESCA",
            "Longitud (WGS84)": "-0,410500",
            "Margen": "D",
            "Municipio": "Huesca",
            "PrecioProducto": "1,529",
            "Provincia": "HUESCA",
            "Remisión": "dm",
            "Rótulo": "REPSOL",
            "Tipo Venta": "P",
            "IDEESS": "1286",
            "IDMunicipio": "3394",
            "IDProvincia": "22",
            "IDCCAA": "02"
        }, {
            ...
        }],
        "Nota": "Archivo de todos los productos en todas las estaciones de servicio. La actualización de precios se realiza cada media hora, con los precios en vigor en ese momento.",
        "ResultadoConsulta": "OK"
    }
*/
