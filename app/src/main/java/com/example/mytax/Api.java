/*
* getting Kommune data from the APi specified using retrofit
* */




package com.example.mytax;



import com.example.mytax.Kommune;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;


public interface Api {
    String BASE_URL = "https://api.myjson.com/bins/";

    @GET("z40as")
    Call<List<Kommune>> getKommune();
}
