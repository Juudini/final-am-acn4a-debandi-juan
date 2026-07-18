package com.example.final_am_acn4a_debandi_juan.data.datasources.firebase.watchlist;

import com.example.final_am_acn4a_debandi_juan.data.mappers.FirestoreMovieMapper;
import com.example.final_am_acn4a_debandi_juan.data.models.Movie;
import com.example.final_am_acn4a_debandi_juan.data.repositories.DataCallback;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public final class FirestoreWatchlistDatasource implements WatchlistDatasource {
    private final FirebaseFirestore firestore;
    private final FirestoreMovieMapper mapper;

    public FirestoreWatchlistDatasource(FirebaseFirestore firestore, FirestoreMovieMapper mapper) {
        this.firestore = firestore;
        this.mapper = mapper;
    }

    @Override
    public void getAll(String uid, DataCallback<List<Movie>> callback) {
        watchlist(uid).get()
            .addOnSuccessListener(snapshot -> {
                List<Movie> movies = new ArrayList<>();
                snapshot.getDocuments().forEach(
                        document -> movies.add(mapper.fromDocument(document)));
                callback.onSuccess(movies);
            })
            .addOnFailureListener(error -> callback.onError(error.getMessage()));
    }

    @Override
    public void add(String uid, Movie movie, DataCallback<Void> callback) {
        watchlist(uid).document(String.valueOf(movie.getId()))
            .set(mapper.toDocument(movie))
            .addOnSuccessListener(ignored -> callback.onSuccess(null))
            .addOnFailureListener(error -> callback.onError(error.getMessage()));
    }

    @Override
    public void remove(String uid, int movieId, DataCallback<Void> callback) {
        watchlist(uid).document(String.valueOf(movieId))
            .delete()
            .addOnSuccessListener(ignored -> callback.onSuccess(null))
            .addOnFailureListener(error -> callback.onError(error.getMessage()));
    }

    @Override
    public void contains(String uid, int movieId, DataCallback<Boolean> callback) {
        watchlist(uid).document(String.valueOf(movieId))
            .get()
            .addOnSuccessListener(document -> callback.onSuccess(document.exists()))
            .addOnFailureListener(error -> callback.onError(error.getMessage()));
    }

    private CollectionReference watchlist(String uid) {
        return firestore.collection("users")
            .document(uid)
            .collection("watchlist");
    }
}
