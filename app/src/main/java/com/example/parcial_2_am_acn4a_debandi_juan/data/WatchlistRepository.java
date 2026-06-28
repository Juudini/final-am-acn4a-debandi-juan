package com.example.parcial_2_am_acn4a_debandi_juan.data;

import androidx.annotation.NonNull;

import com.example.parcial_2_am_acn4a_debandi_juan.data.model.Movie;
import com.example.parcial_2_am_acn4a_debandi_juan.utils.AuthService;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static void add(@NonNull Movie movie, @NonNull ActionCallback callback) {
        CollectionReference ref = watchlistRef();
        if (ref == null) {
            callback.onComplete(false);
            return;
        }
        Map<String, Object> data = new HashMap<>();
        data.put("id", movie.getId());
        data.put("title", movie.getTitle());
        data.put("overview", movie.getOverview());
        data.put("posterPath", movie.getPosterPath());
        data.put("backdropPath", movie.getBackdropPath());
        data.put("voteAverage", movie.getVoteAverage());
        data.put("releaseDate", movie.getReleaseDate());
        ref.document(String.valueOf(movie.getId()))
                .set(data)
                .addOnSuccessListener(unused -> callback.onComplete(true))
                .addOnFailureListener(e -> callback.onComplete(false));
    }

    public static void remove(int movieId, @NonNull ActionCallback callback) {
        CollectionReference ref = watchlistRef();
        if (ref == null) {
            callback.onComplete(false);
            return;
        }
        ref.document(String.valueOf(movieId))
                .delete()
                .addOnSuccessListener(unused -> callback.onComplete(true))
                .addOnFailureListener(e -> callback.onComplete(false));
    }

    public static void isInWatchlist(int movieId, @NonNull CheckCallback callback) {
        CollectionReference ref = watchlistRef();
        if (ref == null) {
            callback.onResult(false);
            return;
        }
        ref.document(String.valueOf(movieId))
                .get()
                .addOnSuccessListener(doc -> callback.onResult(doc.exists()))
                .addOnFailureListener(e -> callback.onResult(false));
    }

    public static void getAll(@NonNull ListCallback callback) {
        CollectionReference ref = watchlistRef();
        if (ref == null) {
            callback.onError(null);
            return;
        }
        ref.get()
                .addOnSuccessListener(snapshot -> {
                    List<Movie> movies = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        movies.add(toMovie(doc));
                    }
                    callback.onResult(movies);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    private static Movie toMovie(DocumentSnapshot doc) {
        Long id = doc.getLong("id");
        Double vote = doc.getDouble("voteAverage");
        return new Movie(
                id != null ? id.intValue() : 0,
                doc.getString("title"),
                doc.getString("overview"),
                doc.getString("posterPath"),
                doc.getString("backdropPath"),
                vote != null ? vote : 0.0,
                doc.getString("releaseDate"));
    }
}