package com.example.final_am_acn4a_debandi_juan.data.datasources.network.dtos;

import com.google.gson.annotations.SerializedName;

public final class CastMemberDto {
    private int id;
    private String name;
    private String character;

    @SerializedName("profile_path")
    private String profilePath;

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCharacter() { return character; }
    public String getProfilePath() { return profilePath; }
}
