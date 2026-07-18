package com.example.final_am_acn4a_debandi_juan.data.datasources.firebase.watchlist;

import com.example.final_am_acn4a_debandi_juan.data.models.Movie;
import com.example.final_am_acn4a_debandi_juan.data.repositories.DataCallback;

import java.util.List;

public interface WatchlistDatasource {
    void getAll(String uid, DataCallback<List<Movie>> callback);
    void add(String uid, Movie movie, DataCallback<Void> callback);
    void remove(String uid, int movieId, DataCallback<Void> callback);
    void contains(String uid, int movieId, DataCallback<Boolean> callback);
}
