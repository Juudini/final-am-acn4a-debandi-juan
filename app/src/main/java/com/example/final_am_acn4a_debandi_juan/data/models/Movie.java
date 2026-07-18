package com.example.final_am_acn4a_debandi_juan.data.models;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

public class Movie implements Serializable {
    private int id;
    private String title;
    private String overview;
    private String posterPath;
    private String backdropPath;
    private double voteAverage;
    private String releaseDate;
    private List<Integer> genreIds;

    public Movie() {
    }

    public Movie(
        int id, String title, String overview, String posterPath, String backdropPath,
        double voteAverage, String releaseDate
    ) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getOverview() { return overview; }
    public String getPosterPath() { return posterPath; }
    public String getBackdropPath() { return backdropPath; }
    public double getVoteAverage() { return voteAverage; }
    public String getReleaseDate() { return releaseDate; }
    public List<Integer> getGenreIds() { return genreIds; }

    public void setGenreIds(List<Integer> genreIds) {
        this.genreIds = genreIds;
    }

    public String getReleaseYear() {
        if (releaseDate != null && releaseDate.length() >= 4) {
            return releaseDate.substring(0, 4);
        }
        return "";
    }

    public String getFormattedRating() {
        return String.format(Locale.US, "%.1f", voteAverage / 2.0);
    }
}