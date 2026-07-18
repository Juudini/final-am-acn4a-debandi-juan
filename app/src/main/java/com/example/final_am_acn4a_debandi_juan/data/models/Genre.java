package com.example.final_am_acn4a_debandi_juan.data.models;

import java.io.Serializable;

public class Genre implements Serializable {
    private int id;
    private String name;

    public Genre() {
    }

    public Genre(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return name;
    }
}
