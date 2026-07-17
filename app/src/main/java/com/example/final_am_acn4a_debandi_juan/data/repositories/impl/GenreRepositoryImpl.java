package com.example.final_am_acn4a_debandi_juan.data.repositories.impl;

import androidx.annotation.NonNull;

import com.example.final_am_acn4a_debandi_juan.data.datasources.network.TmdbDatasource;
import com.example.final_am_acn4a_debandi_juan.data.models.Genre;
import com.example.final_am_acn4a_debandi_juan.data.models.GenreResponse;
import com.example.final_am_acn4a_debandi_juan.data.repositories.DataCallback;
import com.example.final_am_acn4a_debandi_juan.data.repositories.GenreRepository;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class GenreRepositoryImpl implements GenreRepository {
    private final TmdbDatasource networkDatasource;
    public GenreRepositoryImpl(TmdbDatasource networkDatasource) {
        this.networkDatasource = networkDatasource;
    }

    @Override
    public void getGenres(DataCallback<List<Genre>> callback) {
        networkDatasource.getGenres().enqueue(new Callback<GenreResponse>() {
            @Override
            public void onResponse(@NonNull Call<GenreResponse> call, @NonNull Response<GenreResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getGenres() != null
                            ? response.body().getGenres()
                            : Collections.emptyList());
                } else {
                    callback.onError("Can't get the genres");
                }
            }

            @Override
            public void onFailure(@NonNull Call<GenreResponse> call, @NonNull Throwable error) {
                callback.onError(error.getMessage());
            }
        });
    }
}