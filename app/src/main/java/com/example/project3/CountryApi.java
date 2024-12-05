package com.example.project3;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import java.util.List;

public interface CountryApi {
    @GET("name/{country}?fullText=true")
    Call<List<Country>> getCountryByName(@Path("country") String countryName);
}
