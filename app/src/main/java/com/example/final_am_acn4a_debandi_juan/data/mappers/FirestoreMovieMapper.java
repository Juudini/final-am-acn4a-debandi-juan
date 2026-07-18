package com.example.final_am_acn4a_debandi_juan.data.mappers;

import com.example.final_am_acn4a_debandi_juan.data.models.Movie;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class FirestoreMovieMapper {
    public Map<String, Object> toDocument(Movie movie) {
        Map<String, Object> document = new HashMap<>();
        document.put("id", movie.getId());
        document.put("title", movie.getTitle());
        document.put("overview", movie.getOverview());
        document.put("posterPath", movie.getPosterPath());
        document.put("backdropPath", movie.getBackdropPath());
        document.put("voteAverage", movie.getVoteAverage());
        document.put("releaseDate", movie.getReleaseDate());
        document.put("genreIds", movie.getGenreIds());
        return document;
    }

    public Movie fromDocument(DocumentSnapshot document) {
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
