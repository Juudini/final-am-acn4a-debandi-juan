package com.example.final_am_acn4a_debandi_juan.data.mappers;

import com.example.final_am_acn4a_debandi_juan.data.datasources.network.dtos.MovieDetailDto;
import com.example.final_am_acn4a_debandi_juan.data.datasources.network.dtos.MovieDto;
import com.example.final_am_acn4a_debandi_juan.data.models.Movie;
import com.example.final_am_acn4a_debandi_juan.data.models.MovieDetail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class TmdbMovieMapper {
    private final TmdbGenreMapper genreMapper;

    public TmdbMovieMapper(TmdbGenreMapper genreMapper) {
        this.genreMapper = genreMapper;
    }

    public Movie toModel(MovieDto dto) {
        Movie movie = new Movie(
            dto.getId(),
            dto.getTitle(),
            dto.getOverview(),
            dto.getPosterPath(),
            dto.getBackdropPath(),
            dto.getVoteAverage(),
            dto.getReleaseDate()
        );
        movie.setGenreIds(dto.getGenreIds() != null ? new ArrayList<>(dto.getGenreIds()) : Collections.emptyList());
        return movie;
    }

    public List<Movie> toModels(List<MovieDto> dtos) {
        if (dtos == null) {
            return Collections.emptyList();
        }
        List<Movie> movies = new ArrayList<>(dtos.size());
        for (MovieDto dto : dtos) {
            if (dto != null) {
                movies.add(toModel(dto));
            }
        }
        return movies;
    }

    public MovieDetail toModel(MovieDetailDto dto) {
        return new MovieDetail(
            dto.getId(),
            dto.getTitle(),
            dto.getOverview(),
            dto.getPosterPath(),
            dto.getBackdropPath(),
            dto.getVoteAverage(),
            dto.getReleaseDate(),
            dto.getRuntime(),
            dto.getTagline(),
            genreMapper.toModels(dto.getGenres())
        );
    }
}
