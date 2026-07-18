package com.example.final_am_acn4a_debandi_juan.data.mappers;

import com.example.final_am_acn4a_debandi_juan.data.datasources.network.dtos.GenreDto;
import com.example.final_am_acn4a_debandi_juan.data.models.Genre;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class TmdbGenreMapper {
    public Genre toModel(GenreDto dto) {
        return new Genre(dto.getId(), dto.getName());
    }

    public List<Genre> toModels(List<GenreDto> dtos) {
        if (dtos == null) {
            return Collections.emptyList();
        }
        List<Genre> genres = new ArrayList<>(dtos.size());
        for (GenreDto dto : dtos) {
            if (dto != null) {
                genres.add(toModel(dto));
            }
        }
        return genres;
    }
}
