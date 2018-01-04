package com.example.octav.aarhusfitness;

import android.app.Application;

import com.example.octav.aarhusfitness.api.FitnessApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Octav on 1/3/2018.
 */

public class App extends Application {

    private static FitnessApi fitnessApi;
    private Retrofit retrofit;

    @Override
    public void onCreate() {
        super.onCreate();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://portal.opendata.dk/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        fitnessApi = retrofit.create(FitnessApi.class);
    }

    public static FitnessApi getApi() {
        return fitnessApi;
    }
}