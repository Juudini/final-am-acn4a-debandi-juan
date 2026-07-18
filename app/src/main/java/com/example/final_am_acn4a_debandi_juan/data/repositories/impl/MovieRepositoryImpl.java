package com.example.final_am_acn4a_debandi_juan.data.repositories.impl;

import androidx.annotation.NonNull;

import com.example.final_am_acn4a_debandi_juan.data.datasources.network.TmdbDatasource;
import com.example.final_am_acn4a_debandi_juan.data.datasources.network.dtos.CreditsResponseDto;
import com.example.final_am_acn4a_debandi_juan.data.datasources.network.dtos.MovieDetailDto;
import com.example.final_am_acn4a_debandi_juan.data.datasources.network.dtos.MovieResponseDto;
import com.example.final_am_acn4a_debandi_juan.data.mappers.TmdbCastMapper;
import com.example.final_am_acn4a_debandi_juan.data.mappers.TmdbMovieMapper;
import com.example.final_am_acn4a_debandi_juan.data.models.CastMember;
import com.example.final_am_acn4a_debandi_juan.data.models.Movie;
import com.example.final_am_acn4a_debandi_juan.data.models.MovieDetail;
import com.example.final_am_acn4a_debandi_juan.data.repositories.DataCallback;
import com.example.final_am_acn4a_debandi_juan.data.repositories.MovieRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class MovieRepositoryImpl implements MovieRepository {
    private static final String NETWORK_ERROR = "Can't get the data";

    private final TmdbDatasource networkDatasource;
    private final TmdbMovieMapper movieMapper;
    private final TmdbCastMapper castMapper;

    public MovieRepositoryImpl(TmdbDatasource networkDatasource,
                               TmdbMovieMapper movieMapper,
                               TmdbCastMapper castMapper) {
        this.networkDatasource = networkDatasource;
        this.movieMapper = movieMapper;
        this.castMapper = castMapper;
    }

    @Override
    public void getTrending(DataCallback<List<Movie>> callback) {
        networkDatasource.getTrending().enqueue(movieListCallback(callback));
    }

    @Override
    public void getNowPlaying(int page, DataCallback<List<Movie>> callback) {
        networkDatasource.getNowPlaying(page).enqueue(movieListCallback(callback));
    }

    @Override
    public void searchMovies(String query, int page, DataCallback<List<Movie>> callback) {
        networkDatasource.searchMovies(query, page).enqueue(movieListCallback(callback));
    }

    @Override
    public void discoverByGenre(int genreId, String sortBy, int page, DataCallback<List<Movie>> callback) {
        networkDatasource.discoverByGenre(genreId, sortBy, page).enqueue(movieListCallback(callback));
    }

    @Override
    public void getMovieDetail(int movieId, DataCallback<MovieDetail> callback) {
        networkDatasource.getMovieDetail(movieId).enqueue(new Callback<MovieDetailDto>() {
            @Override
            public void onResponse(@NonNull Call<MovieDetailDto> call, @NonNull Response<MovieDetailDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(movieMapper.toModel(response.body()));
                } else {
                    callback.onError(NETWORK_ERROR);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieDetailDto> call, @NonNull Throwable error) {
                callback.onError(error.getMessage());
            }
        });
    }

    @Override
    public void getMovieCredits(int movieId, DataCallback<List<CastMember>> callback) {
        networkDatasource.getMovieCredits(movieId).enqueue(
            new Callback<CreditsResponseDto>() {
                @Override
                public void onResponse(@NonNull Call<CreditsResponseDto> call, @NonNull Response<CreditsResponseDto> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        callback.onSuccess(castMapper.toModels(response.body().getCast()));
                    } else {
                        callback.onError(NETWORK_ERROR);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<CreditsResponseDto> call, @NonNull Throwable error) {
                    callback.onError(error.getMessage());
                }
            });
    }

    private Callback<MovieResponseDto> movieListCallback(DataCallback<List<Movie>> callback) {
        return new Callback<MovieResponseDto>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponseDto> call, @NonNull Response<MovieResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(movieMapper.toModels(response.body().getResults()));
                } else {
                    callback.onError(NETWORK_ERROR);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponseDto> call, @NonNull Throwable error) {
                callback.onError(error.getMessage());
            }
        };
    }
}
