package com.example.final_am_acn4a_debandi_juan.data.repositories.impl;

import com.example.final_am_acn4a_debandi_juan.data.datasources.firebase.watchlist.WatchlistDatasource;
import com.example.final_am_acn4a_debandi_juan.data.models.Genre;
import com.example.final_am_acn4a_debandi_juan.data.models.Movie;
import com.example.final_am_acn4a_debandi_juan.data.repositories.AuthRepository;
import com.example.final_am_acn4a_debandi_juan.data.repositories.DataCallback;
import com.example.final_am_acn4a_debandi_juan.data.repositories.GenreRepository;
import com.example.final_am_acn4a_debandi_juan.data.repositories.WatchlistRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class WatchlistRepositoryImpl implements WatchlistRepository {
    private static final String AUTH_ERROR = "User is not authenticated";

    private final WatchlistDatasource dataSource;
    private final AuthRepository authRepository;
    private final GenreRepository genreRepository;

    public WatchlistRepositoryImpl(
        WatchlistDatasource dataSource,
        AuthRepository authRepository,
        GenreRepository genreRepository
    ) {
        this.dataSource = dataSource;
        this.authRepository = authRepository;
        this.genreRepository = genreRepository;
    }

    @Override
    public void getAll(DataCallback<List<Movie>> callback) {
        String uid = requireUid(callback);
        if (uid == null) {
            return;
        }

        dataSource.getAll(uid, new DataCallback<List<Movie>>() {
            @Override
            public void onSuccess(List<Movie> movies) {
                attachGenreNames(movies != null ? movies : new ArrayList<>(), callback);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
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

    private void attachGenreNames(List<Movie> movies, DataCallback<List<Movie>> callback) {
        if (movies.isEmpty()) {
            callback.onSuccess(movies);
            return;
        }

        genreRepository.getGenres(new DataCallback<List<Genre>>() {
            @Override
            public void onSuccess(List<Genre> genres) {
                applyGenreNames(movies, genres);
                callback.onSuccess(movies);
            }

            @Override
            public void onError(String message) {
                callback.onSuccess(movies);
            }
        });
    }

    private void applyGenreNames(List<Movie> movies, List<Genre> genres) {
        Map<Integer, String> namesById = new HashMap<>();
        if (genres != null) {
            for (Genre genre : genres) {
                if (genre != null && genre.getName() != null) {
                    namesById.put(genre.getId(), genre.getName());
                }
            }
        }

        for (Movie movie : movies) {
            List<String> names = new ArrayList<>();
            if (movie.getGenreIds() != null) {
                for (Integer genreId : movie.getGenreIds()) {
                    String name = namesById.get(genreId);
                    if (name != null) {
                        names.add(name);
                    }
                }
            }
            movie.setGenreNames(names);
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
