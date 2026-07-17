package com.example.final_am_acn4a_debandi_juan.data.repositories;

import com.example.final_am_acn4a_debandi_juan.data.models.CastMember;
import com.example.final_am_acn4a_debandi_juan.data.models.Movie;
import com.example.final_am_acn4a_debandi_juan.data.models.MovieDetail;

import java.util.List;

public interface MovieRepository {
    void getTrending(DataCallback<List<Movie>> callback);
    void getNowPlaying(int page, DataCallback<List<Movie>> callback);
    void searchMovies(String query, int page, DataCallback<List<Movie>> callback);
    void discoverByGenre(int genreId, String sortBy, int page, DataCallback<List<Movie>> callback);
    void getMovieDetail(int movieId, DataCallback<MovieDetail> callback);
    void getMovieCredits(int movieId, DataCallback<List<CastMember>> callback);
}