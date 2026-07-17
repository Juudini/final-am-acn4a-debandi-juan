package com.example.final_am_acn4a_debandi_juan.data.repositories.impl;

import androidx.annotation.NonNull;
import com.example.final_am_acn4a_debandi_juan.data.datasources.network.TmdbDatasource;
import com.example.final_am_acn4a_debandi_juan.data.models.CastMember;
import com.example.final_am_acn4a_debandi_juan.data.models.CreditsResponse;
import com.example.final_am_acn4a_debandi_juan.data.models.Movie;
import com.example.final_am_acn4a_debandi_juan.data.models.MovieDetail;
import com.example.final_am_acn4a_debandi_juan.data.models.MovieResponse;
import com.example.final_am_acn4a_debandi_juan.data.repositories.DataCallback;
import com.example.final_am_acn4a_debandi_juan.data.repositories.MovieRepository;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public final class MovieRepositoryImpl implements MovieRepository {
    private static final String NETWORK_ERROR = "Can't get the data";
    private final TmdbDatasource networkDatasource;

    public MovieRepositoryImpl(TmdbDatasource networkDatasource) {
        this.networkDatasource = networkDatasource;
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
        networkDatasource.getMovieDetail(movieId).enqueue(new Callback<MovieDetail>() {
            @Override
            public void onResponse(@NonNull Call<MovieDetail> call, @NonNull Response<MovieDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(NETWORK_ERROR);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieDetail> call, @NonNull Throwable error) {
                callback.onError(error.getMessage());
            }
        });
    }

    @Override
    public void getMovieCredits(int movieId, DataCallback<List<CastMember>> callback) {
        networkDatasource.getMovieCredits(movieId).enqueue(new Callback<CreditsResponse>() {
            @Override
            public void onResponse(@NonNull Call<CreditsResponse> call, @NonNull Response<CreditsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getCast() != null
                            ? response.body().getCast()
                            : Collections.emptyList());
                } else {
                    callback.onError(NETWORK_ERROR);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CreditsResponse> call, @NonNull Throwable error) {
                callback.onError(error.getMessage());
            }
        });
    }

    private Callback<MovieResponse> movieListCallback(DataCallback<List<Movie>> callback) {
        return new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getResults() != null
                            ? response.body().getResults()
                            : Collections.emptyList());
                } else {
                    callback.onError(NETWORK_ERROR);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable error) {
                callback.onError(error.getMessage());
            }
        };
    }
}
