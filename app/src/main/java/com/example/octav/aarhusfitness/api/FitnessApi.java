package com.example.octav.aarhusfitness.api;

import com.example.octav.aarhusfitness.model.FitnessApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface FitnessApi {
    @GET("dataset/ca1b668e-71d6-4890-b1d2-b222c89ea762/resource/194e7fad-907c-4271-9a55-55fe8f296104/download/fitnessidetfriwgs84.json")
    Call<FitnessApiResponse> getData();
}