package com.example.parcial_2_am_acn4a_debandi_juan.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

public class MovieDetail implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("overview")
    private String overview;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("backdrop_path")
    private String backdropPath;

    @SerializedName("vote_average")
    private double voteAverage;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("runtime")
    private int runtime;

    @SerializedName("tagline")
    private String tagline;

    @SerializedName("genres")
    private List<Genre> genres;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public int getRuntime() {
        return runtime;
    }

    public String getTagline() {
        return tagline;
    }

    public List<Genre> getGenres() {
        return genres;
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
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < genres.size(); i++) {
            if (i > 0) {
                sb.append(" • ");
            }
            sb.append(genres.get(i).getName().toUpperCase(Locale.US));
        }
        return sb.toString();
    }
}