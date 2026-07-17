package com.example.final_am_acn4a_debandi_juan.data.datasources.network;

import com.example.final_am_acn4a_debandi_juan.data.datasources.network.api.TmdbApi;
import com.example.final_am_acn4a_debandi_juan.data.models.CreditsResponse;
import com.example.final_am_acn4a_debandi_juan.data.models.GenreResponse;
import com.example.final_am_acn4a_debandi_juan.data.models.MovieDetail;
import com.example.final_am_acn4a_debandi_juan.data.models.MovieResponse;

import java.util.Objects;

import retrofit2.Call;

public class TmdbDatasourceImpl implements TmdbDatasource {
    private final TmdbApi api;
    public TmdbDatasourceImpl(TmdbApi api) {
        this.api = Objects.requireNonNull(api, "api must not be null");
    }

    @Override
    public Call<MovieResponse> getTrending() {
        return api.getTrending();
    }

    @Override
    public Call<MovieResponse> getNowPlaying(int page) {
        return api.getNowPlaying(page);
    }

    @Override
    public Call<MovieResponse> searchMovies(String query, int page) {
        return api.searchMovies(query, page);
    }

    @Override
    public Call<GenreResponse> getGenres() {
        return api.getGenres();
    }

    @Override
    public Call<MovieResponse> discoverByGenre(int genreId, String sortBy, int page) {
        return api.discoverByGenre(genreId, sortBy, page);
    }

    @Override
    public Call<MovieDetail> getMovieDetail(int movieId) {
        return api.getMovieDetail(movieId);
    }

    @Override
    public Call<CreditsResponse> getMovieCredits(int movieId) {
        return api.getMovieCredits(movieId);
    }
}
