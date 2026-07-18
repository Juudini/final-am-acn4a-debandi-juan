package com.example.final_am_acn4a_debandi_juan.data.datasources.network.dtos;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public final class MovieDto {
    private int id;
    private String title;
    private String overview;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("backdrop_path")
    private String backdropPath;

    @SerializedName("vote_average")
    private double voteAverage;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("genre_ids")
    private List<Integer> genreIds;

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getOverview() { return overview; }
    public String getPosterPath() { return posterPath; }
    public String getBackdropPath() { return backdropPath; }
    public double getVoteAverage() { return voteAverage; }
    public String getReleaseDate() { return releaseDate; }
    public List<Integer> getGenreIds() { return genreIds; }
}
