package com.example.final_am_acn4a_debandi_juan.data.repositories.impl;

import androidx.annotation.NonNull;

import com.example.final_am_acn4a_debandi_juan.data.datasources.network.TmdbDatasource;
import com.example.final_am_acn4a_debandi_juan.data.datasources.network.dtos.GenreResponseDto;
import com.example.final_am_acn4a_debandi_juan.data.mappers.TmdbGenreMapper;
import com.example.final_am_acn4a_debandi_juan.data.models.Genre;
import com.example.final_am_acn4a_debandi_juan.data.repositories.DataCallback;
import com.example.final_am_acn4a_debandi_juan.data.repositories.GenreRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class GenreRepositoryImpl implements GenreRepository {
    private final TmdbDatasource networkDatasource;
    private final TmdbGenreMapper mapper;

    public GenreRepositoryImpl(TmdbDatasource networkDatasource,
                               TmdbGenreMapper mapper) {
        this.networkDatasource = networkDatasource;
        this.mapper = mapper;
    }

    @Override
    public void getGenres(DataCallback<List<Genre>> callback) {
        networkDatasource.getGenres().enqueue(new Callback<GenreResponseDto>() {
            @Override
            public void onResponse(@NonNull Call<GenreResponseDto> call,
                                   @NonNull Response<GenreResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(mapper.toModels(response.body().getGenres()));
                } else {
                    callback.onError("Can't get the genres");
                }
            }

            @Override
            public void onFailure(@NonNull Call<GenreResponseDto> call,
                                  @NonNull Throwable error) {
                callback.onError(error.getMessage());
            }
        });
    }
}