package com.example.project3;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private EditText countryNameEditText;
    private Button showFlagButton;
    private ImageView flagImageView;
    private MapView mapView;
    private GoogleMap googleMap;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        countryNameEditText = findViewById(R.id.country_name);
        showFlagButton = findViewById(R.id.show_flag_button);
        flagImageView = findViewById(R.id.flag_image);
        mapView = findViewById(R.id.mapView);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        showFlagButton.setOnClickListener(v -> {
            String countryName = countryNameEditText.getText().toString().trim();
            if (!countryName.isEmpty()) {
                fetchCountryInfo(countryName);
            } else {
                Toast.makeText(MainActivity.this, "Proszę wpisać nazwę kraju", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchCountryInfo(String countryName) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://restcountries.com/v3.1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CountryApi countryApi = retrofit.create(CountryApi.class);
        Call<List<Country>> call = countryApi.getCountryByName(countryName);

        call.enqueue(new Callback<List<Country>>() {
            @Override
            public void onResponse(@NonNull Call<List<Country>> call, @NonNull Response<List<Country>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Map<String, String> flags = response.body().get(0).getFlags();
                    String flagUrl = flags != null ? flags.get("png") : null;
                    if (flagUrl != null) {
                        Picasso.get().load(flagUrl).into(flagImageView);
                    } else {
                        Toast.makeText(MainActivity.this, "Brak flagi dla tego kraju", Toast.LENGTH_SHORT).show();
                    }

                    // Center map on country
                    Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocationName(countryName, 1);
                        if (!addresses.isEmpty()) {
                            Address location = addresses.get(0);
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Nie znaleziono kraju. Sprawdź nazwę.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Country>> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "Błąd połączenia: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }
        mapView.onSaveInstanceState(mapViewBundle);
    }
}
