package com.example.final_am_acn4a_debandi_juan.data.datasources.network;

import com.example.final_am_acn4a_debandi_juan.data.datasources.network.dtos.CreditsResponseDto;
import com.example.final_am_acn4a_debandi_juan.data.datasources.network.dtos.GenreResponseDto;
import com.example.final_am_acn4a_debandi_juan.data.datasources.network.dtos.MovieDetailDto;
import com.example.final_am_acn4a_debandi_juan.data.datasources.network.dtos.MovieResponseDto;

import retrofit2.Call;

public interface TmdbDatasource {
    Call<MovieResponseDto> getTrending();
    Call<MovieResponseDto> getNowPlaying(int page);
    Call<MovieResponseDto> searchMovies(String query, int page);
    Call<GenreResponseDto> getGenres();
    Call<MovieResponseDto> discoverByGenre(int genreId, String sortBy, int page);
    Call<MovieDetailDto> getMovieDetail(int movieId);
    Call<CreditsResponseDto> getMovieCredits(int movieId);
}
