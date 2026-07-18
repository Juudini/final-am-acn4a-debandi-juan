package com.example.final_am_acn4a_debandi_juan.data.datasources.firebase.watchlist;

import com.example.final_am_acn4a_debandi_juan.data.models.Movie;
import com.example.final_am_acn4a_debandi_juan.data.repositories.DataCallback;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class FirestoreWatchlistDatasource implements WatchlistDatasource {
    private final FirebaseFirestore firestore;

    public FirestoreWatchlistDatasource(FirebaseFirestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public void getAll(String uid, DataCallback<List<Movie>> callback) {
        watchlist(uid).get()
            .addOnSuccessListener(snapshot -> {
                List<Movie> movies = new ArrayList<>();
                for (DocumentSnapshot document : snapshot.getDocuments()) {
                    movies.add(toMovie(document));
                }
                callback.onSuccess(movies);
            })
            .addOnFailureListener(error -> callback.onError(error.getMessage())
        );
    }

    @Override
    public void add(String uid, Movie movie, DataCallback<Void> callback) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", movie.getId());
        data.put("title", movie.getTitle());
        data.put("overview", movie.getOverview());
        data.put("posterPath", movie.getPosterPath());
        data.put("backdropPath", movie.getBackdropPath());
        data.put("voteAverage", movie.getVoteAverage());
        data.put("releaseDate", movie.getReleaseDate());
        data.put("genreIds", movie.getGenreIds());

        watchlist(uid).document(String.valueOf(movie.getId()))
            .set(data)
            .addOnSuccessListener(ignored -> callback.onSuccess(null))
            .addOnFailureListener(error -> callback.onError(error.getMessage())
        );
    }

    @Override
    public void remove(String uid, int movieId, DataCallback<Void> callback) {
        watchlist(uid).document(String.valueOf(movieId))
            .delete()
            .addOnSuccessListener(ignored -> callback.onSuccess(null))
            .addOnFailureListener(error -> callback.onError(error.getMessage())
        );
    }

    @Override
    public void contains(String uid, int movieId, DataCallback<Boolean> callback) {
        watchlist(uid).document(String.valueOf(movieId))
            .get()
            .addOnSuccessListener(document -> callback.onSuccess(document.exists()))
            .addOnFailureListener(error -> callback.onError(error.getMessage())
        );
    }

    private CollectionReference watchlist(String uid) {
        return firestore.collection("users").document(uid).collection("watchlist");
    }

    private Movie toMovie(DocumentSnapshot document) {
        Long id = document.getLong("id");
        Double voteAverage = document.getDouble("voteAverage");
        List<Integer> genreIds = new ArrayList<>();
        Object rawGenreIds = document.get("genreIds");
        if (rawGenreIds instanceof List<?>) {
            for (Object value : (List<?>) rawGenreIds) {
                if (value instanceof Number) {
                    genreIds.add(((Number) value).intValue());
                }
            }
        }

        Movie movie = new Movie(
            id != null ? id.intValue() : 0,
            document.getString("title"),
            document.getString("overview"),
            document.getString("posterPath"),
            document.getString("backdropPath"),
            voteAverage != null ? voteAverage : 0.0,
            document.getString("releaseDate")
        );
        movie.setGenreIds(genreIds);
        return movie;
    }
}
