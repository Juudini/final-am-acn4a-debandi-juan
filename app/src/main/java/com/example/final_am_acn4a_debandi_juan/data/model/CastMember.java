package com.example.final_am_acn4a_debandi_juan.data.model;

import com.google.gson.annotations.SerializedName;

public class CastMember {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("character")
    private String character;

    @SerializedName("profile_path")
    private String profilePath;

    public int getId() {
        return id;
    }

    public String getName() {
        return name != null ? name : "";
    }

    public String getCharacter() {
        return character != null ? character : "";
    }

    public String getProfilePath() {
        return profilePath;
    }
}
