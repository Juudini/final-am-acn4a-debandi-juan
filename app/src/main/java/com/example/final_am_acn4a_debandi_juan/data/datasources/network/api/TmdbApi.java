package com.example.final_am_acn4a_debandi_juan.data.datasources.network.api;

import com.example.final_am_acn4a_debandi_juan.data.datasources.network.dtos.CreditsResponseDto;
import com.example.final_am_acn4a_debandi_juan.data.datasources.network.dtos.GenreResponseDto;
import com.example.final_am_acn4a_debandi_juan.data.datasources.network.dtos.MovieDetailDto;
import com.example.final_am_acn4a_debandi_juan.data.datasources.network.dtos.MovieResponseDto;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TmdbApi {
    @GET("trending/movie/week")
    Call<MovieResponseDto> getTrending();

    @GET("movie/now_playing")
    Call<MovieResponseDto> getNowPlaying(@Query("page") int page);

    @GET("search/movie")
    Call<MovieResponseDto> searchMovies(
        @Query("query") String query,
        @Query("page") int page
    );

    @GET("genre/movie/list")
    Call<GenreResponseDto> getGenres();

    @GET("discover/movie")
    Call<MovieResponseDto> discoverByGenre(
        @Query("with_genres") int genreId,
        @Query("sort_by") String sortBy,
        @Query("page") int page
    );

    @GET("movie/{movie_id}")
    Call<MovieDetailDto> getMovieDetail(@Path("movie_id") int movieId);

    @GET("movie/{movie_id}/credits")
    Call<CreditsResponseDto> getMovieCredits(@Path("movie_id") int movieId);
}