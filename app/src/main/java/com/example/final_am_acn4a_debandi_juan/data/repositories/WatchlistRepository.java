package com.example.final_am_acn4a_debandi_juan.data.repositories;

import com.example.final_am_acn4a_debandi_juan.data.models.Movie;

import java.util.List;

public interface WatchlistRepository {
    void getAll(DataCallback<List<Movie>> callback);
    void add(Movie movie, DataCallback<Void> callback);
    void remove(int movieId, DataCallback<Void> callback);
    void contains(int movieId, DataCallback<Boolean> callback);
}
