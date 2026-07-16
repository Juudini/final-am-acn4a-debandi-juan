package com.example.final_am_acn4a_debandi_juan.data.network;

import com.example.final_am_acn4a_debandi_juan.data.model.CreditsResponse;
import com.example.final_am_acn4a_debandi_juan.data.model.GenreResponse;
import com.example.final_am_acn4a_debandi_juan.data.model.MovieDetail;
import com.example.final_am_acn4a_debandi_juan.data.model.MovieResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TmdbApi {

    @GET("trending/movie/week")
    Call<MovieResponse> getTrending();

    @GET("movie/now_playing")
    Call<MovieResponse> getNowPlaying(@Query("page") int page);

    @GET("search/movie")
    Call<MovieResponse> searchMovies(@Query("query") String query, @Query("page") int page);

    @GET("genre/movie/list")
    Call<GenreResponse> getGenres();

    @GET("discover/movie")
    Call<MovieResponse> discoverByGenre(
            @Query("with_genres") int genreId,
            @Query("sort_by") String sortBy,
            @Query("page") int page);

    @GET("movie/{movie_id}")
    Call<MovieDetail> getMovieDetail(@Path("movie_id") int movieId);

    @GET("movie/{movie_id}/credits")
    Call<CreditsResponse> getMovieCredits(@Path("movie_id") int movieId);
}