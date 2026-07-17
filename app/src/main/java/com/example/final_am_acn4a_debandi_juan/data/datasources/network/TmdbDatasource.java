package com.example.final_am_acn4a_debandi_juan.data.datasources.network;

import com.example.final_am_acn4a_debandi_juan.data.models.CreditsResponse;
import com.example.final_am_acn4a_debandi_juan.data.models.GenreResponse;
import com.example.final_am_acn4a_debandi_juan.data.models.MovieDetail;
import com.example.final_am_acn4a_debandi_juan.data.models.MovieResponse;

import retrofit2.Call;

public interface TmdbDatasource {
    Call<MovieResponse> getTrending();
    Call<MovieResponse> getNowPlaying(int page);
    Call<MovieResponse> searchMovies(String query, int page);
    Call<GenreResponse> getGenres();
    Call<MovieResponse> discoverByGenre(int genreId, String sortBy, int page);
    Call<MovieDetail> getMovieDetail(int movieId);
    Call<CreditsResponse> getMovieCredits(int movieId);
}
