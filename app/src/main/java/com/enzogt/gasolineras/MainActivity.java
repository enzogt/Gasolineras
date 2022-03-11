package com.enzogt.gasolineras;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.enzogt.gasolineras.classes.SearchResults;
import com.enzogt.gasolineras.classes.Settings;
import com.enzogt.gasolineras.util.MySharedPreferences;
import com.google.android.material.snackbar.Snackbar;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.settings = MySharedPreferences.readSettings(this);
        setTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.cs1, R.color.cs2, R.color.cs3);
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(getColor(R.color.cbg));
        swipeRefreshLayout.setOnRefreshListener(this::downloadProductPrices);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        /*
            ########## NOTA ########## (https://i.stack.imgur.com/iVKNK.png)
            El método onCreateOptionsMenu se llama después del metodo onCreate e incluso después
            del metodo onResume, por lo que al ir a interactuar con el menu, aún no ha pasado por
            aquí y actionBarMenu por tanto es null.
        */

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        final SearchResults searchResults = MySharedPreferences.readSearchResults(this);
        final Settings settings = MySharedPreferences.readSettings(this);


        if (settings == null) {
            showSettingsDialog();
        } else if (searchResults != null) {
            displayProductPrices(searchResults);
            showSnackBar(getString(R.string.msg_current_prices));
        } else {
            showSnackBar(getString(R.string.msg_update_list_info));
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.show_settings) {
            showSettingsDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setTheme(){

        if (this.settings != null && this.settings.getTheme() == Settings.THEME_BRIGHT) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (this.settings != null &&  this.settings.getTheme() == Settings.THEME_DARK) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    private void setTitleActivity(String producto, String municipio, Date fecha){

        @SuppressLint("ResourceType") String titleColor = getResources().getString(R.color.title_color);
        @SuppressLint("ResourceType") String subtitleColor = getResources().getString(R.color.subtitle_color);

        // Al obtener el string se pone la capa alpha 100% (#FF...) pero no es compatible con html
        titleColor = titleColor.replace("#ff", "#");
        subtitleColor = subtitleColor.replace("#ff", "#");

        String title = producto + " - " + municipio;
        String subtitle = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(fecha);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle(Html.fromHtml("<font color='" + titleColor + "'>" + title + "</font>"));
            actionBar.setSubtitle(Html.fromHtml("<font color='" + subtitleColor + "'>" + subtitle + "</font>"));
        }
    }

    private void showSettingsDialog(){

        Intent intent = new Intent(this, SettingsActivity.class);
        SettingsResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> SettingsResultLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                this.settings = MySharedPreferences.readSettings(this);
                if (this.settings != null) {
                    setTheme();
                    showSnackBar(getString(R.string.msg_update_list_info));
                    //swipeRefreshLayout.post(() -> {
                    //    swipeRefreshLayout.setRefreshing(true);
                    //    downloadProductPrices();
                    //});
                }
            }
        }
    );

    private void downloadProductPrices() {

        if (this.settings == null || this.settings.getIdProduct() < 1 || this.settings.getIdLocation() < 1) {
            swipeRefreshLayout.setRefreshing(false);
            showSnackBar(getString(R.string.msg_settings_not_set));
            return;
        }

        final String apiUrl = SearchResults.URL_API
                .replace("{idProduct}", String.valueOf(this.settings.getIdProduct()))
                .replace("{idLocation}", String.valueOf(this.settings.getIdLocation()));

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, apiUrl,
            response -> {
                SearchResults searchResults = new SearchResults(response, settings.getNameProduct(), settings.getNameLocation());
                MySharedPreferences.saveSearchResults(this, searchResults);
                displayProductPrices(searchResults);
                swipeRefreshLayout.setRefreshing(false);
                showSnackBar(getString(R.string.msg_updated_prices));
            },
            error -> {
                swipeRefreshLayout.setRefreshing(false);
                showSnackBar(getString(R.string.msg_error_get_prices));
            }
        );
        queue.add(stringRequest);
    }

    private void displayProductPrices(SearchResults searchResults) {

        setTitleActivity(searchResults.getProductName(), searchResults.getLocationName(), searchResults.getDate());

        LinearLayout vList = (LinearLayout)findViewById(R.id.list_cards);
        vList.removeAllViews();

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.FRENCH);
        nf.setMaximumFractionDigits(3);
        nf.setMinimumFractionDigits(3);

        for (SearchResults.GasStation gasStation : searchResults.getGasStations()) {

            View vCard = getLayoutInflater().inflate(R.layout.list_item_card, vList, false);

            ((TextView)vCard.findViewById(R.id.lbl_name)).setText(gasStation.getName());
            ((TextView)vCard.findViewById(R.id.lbl_street)).setText(gasStation.getStreet());
            ((TextView)vCard.findViewById(R.id.lbl_price)).setText(String.format("%s €", nf.format(gasStation.getPrice())));

            vList.addView(vCard);
        }
    }

    private void showSnackBar(String msg){
        Snackbar.make(findViewById(R.id.main_root), msg, Snackbar.LENGTH_LONG).show();
    }

}
