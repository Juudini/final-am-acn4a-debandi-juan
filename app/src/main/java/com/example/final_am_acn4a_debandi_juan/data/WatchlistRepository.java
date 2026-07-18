package com.example.final_am_acn4a_debandi_juan.data;

import androidx.annotation.NonNull;

import com.example.final_am_acn4a_debandi_juan.data.mappers.FirestoreMovieMapper;
import com.example.final_am_acn4a_debandi_juan.data.models.Movie;
import com.example.final_am_acn4a_debandi_juan.utils.AuthService;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public final class WatchlistRepository {
    public interface ActionCallback {
        void onComplete(boolean success);
    }

    public interface CheckCallback {
        void onResult(boolean inWatchlist);
    }

    public interface ListCallback {
        void onResult(List<Movie> movies);
        void onError(String message);
    }

    private static final FirestoreMovieMapper mapper = new FirestoreMovieMapper();

    private WatchlistRepository() {
    }

    private static CollectionReference watchlistRef() {
        String uid = AuthService.getUid();
        if (uid == null) {
            return null;
        }
        return FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("watchlist");
    }

    public static void add(@NonNull Movie movie,
                           @NonNull ActionCallback callback) {
        CollectionReference reference = watchlistRef();
        if (reference == null) {
            callback.onComplete(false);
            return;
        }
        reference.document(String.valueOf(movie.getId()))
                .set(mapper.toDocument(movie))
                .addOnSuccessListener(ignored -> callback.onComplete(true))
                .addOnFailureListener(error -> callback.onComplete(false));
    }

    public static void remove(int movieId,
                              @NonNull ActionCallback callback) {
        CollectionReference reference = watchlistRef();
        if (reference == null) {
            callback.onComplete(false);
            return;
        }
        reference.document(String.valueOf(movieId))
                .delete()
                .addOnSuccessListener(ignored -> callback.onComplete(true))
                .addOnFailureListener(error -> callback.onComplete(false));
    }

    public static void isInWatchlist(int movieId,
                                     @NonNull CheckCallback callback) {
        CollectionReference reference = watchlistRef();
        if (reference == null) {
            callback.onResult(false);
            return;
        }
        reference.document(String.valueOf(movieId))
                .get()
                .addOnSuccessListener(document ->
                        callback.onResult(document.exists()))
                .addOnFailureListener(error -> callback.onResult(false));
    }

    public static void getAll(@NonNull ListCallback callback) {
        CollectionReference reference = watchlistRef();
        if (reference == null) {
            callback.onError(null);
            return;
        }
        reference.get()
                .addOnSuccessListener(snapshot -> {
                    List<Movie> movies = new ArrayList<>();
                    snapshot.getDocuments().forEach(
                            document -> movies.add(mapper.fromDocument(document)));
                    callback.onResult(movies);
                })
                .addOnFailureListener(error ->
                        callback.onError(error.getMessage()));
    }
}