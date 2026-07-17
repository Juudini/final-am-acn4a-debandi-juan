package com.example.final_am_acn4a_debandi_juan.data.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GenreResponse {
    @SerializedName("genres")
    private List<Genre> genres;

    public List<Genre> getGenres() {
        return genres;
    }
}