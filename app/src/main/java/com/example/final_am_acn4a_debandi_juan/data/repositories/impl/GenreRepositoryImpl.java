package com.example.final_am_acn4a_debandi_juan.data.repositories.impl;

import androidx.annotation.NonNull;

import com.example.final_am_acn4a_debandi_juan.data.datasources.network.TmdbDatasource;
import com.example.final_am_acn4a_debandi_juan.data.datasources.network.dtos.GenreResponseDto;
import com.example.final_am_acn4a_debandi_juan.data.mappers.TmdbGenreMapper;
import com.example.final_am_acn4a_debandi_juan.data.models.Genre;
import com.example.final_am_acn4a_debandi_juan.data.repositories.DataCallback;
import com.example.final_am_acn4a_debandi_juan.data.repositories.GenreRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class GenreRepositoryImpl implements GenreRepository {
    private static final String NETWORK_ERROR = "Can't get the genres";

    private final TmdbDatasource networkDatasource;
    private final TmdbGenreMapper mapper;
    private final List<Genre> cachedGenres = new ArrayList<>();
    private final List<DataCallback<List<Genre>>> pendingCallbacks = new ArrayList<>();
    private boolean loaded;
    private boolean loading;

    public GenreRepositoryImpl(TmdbDatasource networkDatasource, TmdbGenreMapper mapper) {
        this.networkDatasource = networkDatasource;
        this.mapper = mapper;
    }

    @Override
    public void getGenres(DataCallback<List<Genre>> callback) {
        if (loaded) {
            callback.onSuccess(new ArrayList<>(cachedGenres));
            return;
        }

        pendingCallbacks.add(callback);
        if (loading) {
            return;
        }

        loading = true;
        networkDatasource.getGenres().enqueue(new Callback<GenreResponseDto>() {
            @Override
            public void onResponse(@NonNull Call<GenreResponseDto> call,
                                   @NonNull Response<GenreResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cachedGenres.clear();
                    cachedGenres.addAll(mapper.toModels(response.body().getGenres()));
                    loaded = true;
                    loading = false;
                    notifySuccess();
                } else {
                    loading = false;
                    notifyError(NETWORK_ERROR);
                }
            }

            @Override
            public void onFailure(@NonNull Call<GenreResponseDto> call, @NonNull Throwable error) {
                loading = false;
                notifyError(error.getMessage() != null ? error.getMessage() : NETWORK_ERROR);
            }
        });
    }

    private void notifySuccess() {
        List<DataCallback<List<Genre>>> callbacks = new ArrayList<>(pendingCallbacks);
        pendingCallbacks.clear();
        for (DataCallback<List<Genre>> callback : callbacks) {
            callback.onSuccess(new ArrayList<>(cachedGenres));
        }
    }

    private void notifyError(String message) {
        List<DataCallback<List<Genre>>> callbacks = new ArrayList<>(pendingCallbacks);
        pendingCallbacks.clear();
        for (DataCallback<List<Genre>> callback : callbacks) {
            callback.onError(message);
        }
    }
}
