package com.example.octav.aarhusfitness;

import android.app.Application;

import com.example.octav.aarhusfitness.api.FitnessApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends Application {

    private static final String BASE_OPENDATA_URL = "http://portal.opendata.dk/";
    private static FitnessApi fitnessApi;

    @Override
    public void onCreate() {
        super.onCreate();

        fitnessApi = createFitnessApi();
    }

    private FitnessApi createFitnessApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(App.BASE_OPENDATA_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(FitnessApi.class);
    }

    public static FitnessApi getApi() {
        return fitnessApi;
    }
}