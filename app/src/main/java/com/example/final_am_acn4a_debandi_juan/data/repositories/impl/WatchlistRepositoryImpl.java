package com.example.final_am_acn4a_debandi_juan.data.repositories.impl;

import com.example.final_am_acn4a_debandi_juan.data.datasources.firebase.watchlist.WatchlistDatasource;
import com.example.final_am_acn4a_debandi_juan.data.models.Movie;
import com.example.final_am_acn4a_debandi_juan.data.repositories.AuthRepository;
import com.example.final_am_acn4a_debandi_juan.data.repositories.DataCallback;
import com.example.final_am_acn4a_debandi_juan.data.repositories.WatchlistRepository;

import java.util.List;

public final class WatchlistRepositoryImpl implements WatchlistRepository {
    private static final String AUTH_ERROR = "User is not authenticated";

    private final WatchlistDatasource dataSource;
    private final AuthRepository authRepository;

    public WatchlistRepositoryImpl(WatchlistDatasource dataSource, AuthRepository authRepository) {
        this.dataSource = dataSource;
        this.authRepository = authRepository;
    }

    @Override
    public void getAll(DataCallback<List<Movie>> callback) {
        String uid = requireUid(callback);
        if (uid != null) {
            dataSource.getAll(uid, callback);
        }
    }

    @Override
    public void add(Movie movie, DataCallback<Void> callback) {
        String uid = requireUid(callback);
        if (uid != null) {
            dataSource.add(uid, movie, callback);
        }
    }

    @Override
    public void remove(int movieId, DataCallback<Void> callback) {
        String uid = requireUid(callback);
        if (uid != null) {
            dataSource.remove(uid, movieId, callback);
        }
    }

    @Override
    public void contains(int movieId, DataCallback<Boolean> callback) {
        String uid = requireUid(callback);
        if (uid != null) {
            dataSource.contains(uid, movieId, callback);
        }
    }

    private String requireUid(DataCallback<?> callback) {
        String uid = authRepository.getUid();
        if (uid == null) {
            callback.onError(AUTH_ERROR);
        }
        return uid;
    }
}
