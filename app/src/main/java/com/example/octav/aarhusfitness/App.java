package com.example.octav.aarhusfitness;

import android.app.Application;

import com.example.octav.aarhusfitness.api.FitnessApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends Application {

    private static final String BASE_OPENDATA_URL = "http://portal.opendata.dk/";
    public static final String FITNESS_PLACE_ARG = "fitnessPlace";
    public static final String FIREBASE_DATE_ARG = "firebaseDate";
    private static FitnessApi fitnessApi;
    private static FirebaseHelper firebaseHelper;

    @Override
    public void onCreate() {
        super.onCreate();

        fitnessApi = createFitnessApi();
        firebaseHelper = new FirebaseHelper();
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
    public static FirebaseHelper getFirebaseHelper() { return firebaseHelper; }

}