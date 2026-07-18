package com.example.final_am_acn4a_debandi_juan.data.repositories;
import com.example.final_am_acn4a_debandi_juan.data.models.Genre;

import java.util.List;

public interface GenreRepository {
    void getGenres(DataCallback<List<Genre>> callback);
}
