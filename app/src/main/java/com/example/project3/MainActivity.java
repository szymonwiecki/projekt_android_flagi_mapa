package com.example.project3;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private EditText countryNameEditText;
    private Button showFlagButton;
    private ImageView flagImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicjalizacja widoków
        countryNameEditText = findViewById(R.id.country_name);
        showFlagButton = findViewById(R.id.show_flag_button);
        flagImageView = findViewById(R.id.flag_image);

        // Obsługa kliknięcia przycisku
        showFlagButton.setOnClickListener(v -> {
            String countryName = countryNameEditText.getText().toString().trim();
            if (!countryName.isEmpty()) {
                fetchFlag(countryName);  // Pobierz flagę, jeśli wpisano nazwę kraju
            } else {
                Toast.makeText(MainActivity.this, "Proszę wpisać nazwę kraju", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchFlag(String countryName) {
        // Tworzymy instancję Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://restcountries.com/v3.1/")  // Bazowy URL API
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Tworzymy interfejs API
        CountryApi countryApi = retrofit.create(CountryApi.class);

        // Wykonujemy zapytanie do API
        Call<List<Country>> call = countryApi.getCountryByName(countryName);

        // Obsługuje odpowiedź API
        call.enqueue(new Callback<List<Country>>() {
            @Override
            public void onResponse(Call<List<Country>> call, Response<List<Country>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    // Pobieramy URL flagi z odpowiedzi
                    Map<String, String> flags = response.body().get(0).getFlags();
                    String flagUrl = flags != null ? flags.get("png") : null;  // Link do flagi w formacie PNG

                    if (flagUrl != null) {
                        // Ładujemy flagę do ImageView za pomocą Picasso
                        Picasso.get().load(flagUrl).into(flagImageView);
                    } else {
                        // Jeśli nie znaleziono flagi
                        Toast.makeText(MainActivity.this, "Brak flagi dla tego kraju", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Jeśli kraj nie został znaleziony
                    Toast.makeText(MainActivity.this, "Nie znaleziono kraju. Sprawdź nazwę.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Country>> call, Throwable t) {
                // Jeśli wystąpił błąd połączenia
                Toast.makeText(MainActivity.this, "Błąd połączenia: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
