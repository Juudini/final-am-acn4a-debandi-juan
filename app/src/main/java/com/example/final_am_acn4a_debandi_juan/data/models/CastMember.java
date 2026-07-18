package com.example.final_am_acn4a_debandi_juan.data.models;

public class CastMember {
    private int id;
    private String name;
    private String character;
    private String profilePath;

    public CastMember(int id, String name, String character, String profilePath) {
        this.id = id;
        this.name = name;
        this.character = character;
        this.profilePath = profilePath;
    }

    public int getId() { return id; }
    public String getName() { return name != null ? name : ""; }
    public String getCharacter() { return character != null ? character : ""; }
    public String getProfilePath() { return profilePath; }
}
