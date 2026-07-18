package com.example.final_am_acn4a_debandi_juan.data.datasources.network;

import com.example.final_am_acn4a_debandi_juan.data.datasources.network.api.TmdbApi;
import com.example.final_am_acn4a_debandi_juan.data.datasources.network.dtos.CreditsResponseDto;
import com.example.final_am_acn4a_debandi_juan.data.datasources.network.dtos.GenreResponseDto;
import com.example.final_am_acn4a_debandi_juan.data.datasources.network.dtos.MovieDetailDto;
import com.example.final_am_acn4a_debandi_juan.data.datasources.network.dtos.MovieResponseDto;

import java.util.Objects;

import retrofit2.Call;

public final class TmdbDatasourceImpl implements TmdbDatasource {
    private final TmdbApi api;

    public TmdbDatasourceImpl(TmdbApi api) {
        this.api = Objects.requireNonNull(api, "api must not be null");
    }

    @Override public Call<MovieResponseDto> getTrending() { return api.getTrending(); }
    @Override public Call<MovieResponseDto> getNowPlaying(int page) { return api.getNowPlaying(page); }
    @Override public Call<MovieResponseDto> searchMovies(String query, int page) { return api.searchMovies(query, page); }
    @Override public Call<GenreResponseDto> getGenres() { return api.getGenres(); }
    @Override public Call<MovieResponseDto> discoverByGenre(int genreId, String sortBy, int page) {
        return api.discoverByGenre(genreId, sortBy, page);
    }
    @Override public Call<MovieDetailDto> getMovieDetail(int movieId) { return api.getMovieDetail(movieId); }
    @Override public Call<CreditsResponseDto> getMovieCredits(int movieId) { return api.getMovieCredits(movieId); }
}
