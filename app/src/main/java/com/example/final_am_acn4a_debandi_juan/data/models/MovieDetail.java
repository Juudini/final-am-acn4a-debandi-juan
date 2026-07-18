package com.example.final_am_acn4a_debandi_juan.data.models;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

public class MovieDetail implements Serializable {
    private int id;
    private String title;
    private String overview;
    private String posterPath;
    private String backdropPath;
    private double voteAverage;
    private String releaseDate;
    private int runtime;
    private String tagline;
    private List<Genre> genres;

    public MovieDetail(
        int id, String title, String overview, String posterPath,
        String backdropPath, double voteAverage, String releaseDate,
        int runtime, String tagline, List<Genre> genres
    ) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.runtime = runtime;
        this.tagline = tagline;
        this.genres = genres;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getOverview() { return overview; }
    public String getPosterPath() { return posterPath; }
    public String getBackdropPath() { return backdropPath; }
    public double getVoteAverage() { return voteAverage; }
    public String getReleaseDate() { return releaseDate; }
    public int getRuntime() { return runtime; }
    public String getTagline() { return tagline; }
    public List<Genre> getGenres() { return genres; }

    public String getReleaseYear() {
        if (releaseDate != null && releaseDate.length() >= 4) {
            return releaseDate.substring(0, 4);
        }
        return "";
    }

    public String getFormattedRating() {
        return String.format(Locale.US, "%.1f", voteAverage / 2.0);
    }

    public String getFormattedRuntime() {
        if (runtime <= 0) {
            return "";
        }
        int hours = runtime / 60;
        int minutes = runtime % 60;
        if (hours > 0) {
            return hours + "H " + minutes + "M";
        }
        return minutes + "M";
    }

    public String getGenresLabel() {
        if (genres == null || genres.isEmpty()) {
            return "";
        }
        StringBuilder label = new StringBuilder();
        for (int index = 0; index < genres.size(); index++) {
            if (index > 0) {
                label.append(" • ");
            }
            label.append(genres.get(index).getName().toUpperCase(Locale.US));
        }
        return label.toString();
    }
}