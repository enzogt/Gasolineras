package com.enzogt.gasolineras;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.enzogt.gasolineras.classes.Locations;
import com.enzogt.gasolineras.classes.Products;
import com.enzogt.gasolineras.classes.Settings;
import com.enzogt.gasolineras.classes.SpanishArrayAdapter;
import com.enzogt.gasolineras.util.MySharedPreferences;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;


public class SettingsActivity extends AppCompatActivity {

    RadioButton rbThemeAuto, rbThemeBright, rbThemeDark;
    AutoCompleteTextView inputProduct, inputLocation;
    Button btnSave, btnDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        rbThemeAuto = ((RadioButton)findViewById(R.id.rb_theme_auto));
        rbThemeBright = ((RadioButton)findViewById(R.id.rb_theme_bright));
        rbThemeDark = ((RadioButton)findViewById(R.id.rb_theme_dark));

        inputProduct = ((AutoCompleteTextView)findViewById(R.id.input_product));
        inputLocation = ((AutoCompleteTextView)findViewById(R.id.input_location));

        btnSave = ((Button)findViewById(R.id.btn_save));
        btnSave.setOnClickListener(this::saveForm);

        btnDownload = ((Button)findViewById(R.id.btn_download_api));
        btnDownload.setOnClickListener(this::downloadApiData);

        setAdapterProducts();
        setAdapterLocations();
        loadForm();
    }

    private void loadForm () {

        Settings settings = MySharedPreferences.readSettings(this);

        if (settings != null) {

            rbThemeAuto.setChecked(settings.getTheme() == Settings.THEME_AUTO);
            rbThemeBright.setChecked(settings.getTheme() == Settings.THEME_BRIGHT);
            rbThemeDark.setChecked(settings.getTheme() == Settings.THEME_DARK);

            inputProduct.setText(settings.getNameProduct());
            inputLocation.setText(settings.getNameLocation());

        } else {
            rbThemeAuto.setChecked(true);
        }
    }

    private void saveForm(View v){

        final Products productsList = MySharedPreferences.readProducts(this);
        final Locations locationsList = MySharedPreferences.readLocations(this);

        final boolean themeAuto = rbThemeAuto.isChecked();
        final boolean themeBright = rbThemeBright.isChecked();
        final boolean themeDark = rbThemeDark.isChecked();

        final String product = inputProduct.getText().toString();
        final String location = inputLocation.getText().toString();

        // Validación formulario

        int idProduct = (productsList == null ? -1 : productsList.getIdProductByName(product));
        int idLocation = (locationsList == null ? -1 : locationsList.getIdLocationByName(location));

        int error = -1;

        if (productsList == null || locationsList == null)
            error = R.string.msg_form_empty_list_api;
        else if (!themeAuto && !themeBright && !themeDark)
            error = R.string.msg_form_empty_theme;
        else if (product.length() == 0 || idProduct < 0)
            error = R.string.msg_form_empty_product;
        else if  (location.length() == 0|| idLocation < 0)
            error = R.string.msg_form_empty_location;

        if (error > 0) {
            mostrarSnackBar(getString(error), "error");
            return;
        }

        // Guardado formulario

        Settings settings = new Settings();

        if (themeBright) {
            settings.setTheme(Settings.THEME_BRIGHT);
        } else if (themeDark) {
            settings.setTheme(Settings.THEME_DARK);
        } else {
            settings.setTheme(Settings.THEME_AUTO);
        }

        settings.setIdProduct(idProduct);
        settings.setNameProduct(product);

        settings.setIdLocation(idLocation);
        settings.setNameLocation(location);

        MySharedPreferences.saveSettings(this, settings);

        setResult(Activity.RESULT_OK);
        finish();
    }

    private void downloadApiData(View view) {
        getProductsApi();
        getLocationsApi();
    }

    private void setAdapterProducts(){

        Products products = MySharedPreferences.readProducts(this);

        if (products == null) {
            getProductsApi();
            return;
        }

        ArrayList<String> productsNames = new ArrayList<>();

        for (Products.Product product : products.getList()) {
            productsNames.add(product.getName());
        }

        SpanishArrayAdapter<String> adapter = new SpanishArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, productsNames);
        inputProduct.setAdapter(adapter);
    }

    private void setAdapterLocations(){

        Locations locations = MySharedPreferences.readLocations(this);

        if (locations == null) {
            getLocationsApi();
            return;
        }

        ArrayList<String> locationsNames = new ArrayList<>();

        for (Locations.Location location : locations.getList()) {
            locationsNames.add(location.getName());
        }

        SpanishArrayAdapter<String> adapter = new SpanishArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, locationsNames);
        inputLocation.setAdapter(adapter);
    }

    private void getProductsApi () {
        mostrarSnackBar(getString(R.string.msg_downloading_api_data), "info");
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Products.URL_API,
            response -> {
                //System.out.println(response);
                Products products = new Products(response);
                MySharedPreferences.saveProducts(this, products);
                setAdapterProducts();
            },
            error -> mostrarSnackBar(getString(R.string.msg_error_get_products), "error")
        );
        queue.add(stringRequest);
    }

    private void getLocationsApi () {
        mostrarSnackBar(getString(R.string.msg_downloading_api_data), "info");
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Locations.URL_API,
            response -> {
                //System.out.println(response);
                Locations locations = new Locations(response);
                MySharedPreferences.saveLocations(this, locations);
                setAdapterLocations();
            },
            error -> mostrarSnackBar(getString(R.string.msg_error_get_locations), "error")
        );
        queue.add(stringRequest);
    }

    private void mostrarSnackBar(String textoSnackBar, String type){

        Snackbar snackbar = Snackbar.make(findViewById(R.id.settings_root), textoSnackBar, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(this, "error".equals(type) ? R.color.snackbar_error : R.color.snackbar_info));

        snackbar.show();
    }

}