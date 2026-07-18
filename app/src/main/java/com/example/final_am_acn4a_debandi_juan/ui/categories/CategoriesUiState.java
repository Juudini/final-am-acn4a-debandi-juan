package com.example.final_am_acn4a_debandi_juan.ui.categories;

import com.example.final_am_acn4a_debandi_juan.data.models.Genre;
import com.example.final_am_acn4a_debandi_juan.ui.common.state.UiStatus;

import java.util.Collections;
import java.util.List;

public final class CategoriesUiState {
    private final UiStatus status;
    private final List<Genre> genres;
    private final String message;

    public CategoriesUiState(UiStatus status, List<Genre> genres, String message) {
        this.status = status;
        this.genres = genres != null ? genres : Collections.emptyList();
        this.message = message;
    }

    public static CategoriesUiState loading() {
        return new CategoriesUiState(UiStatus.LOADING, null, null);
    }

    public static CategoriesUiState content(List<Genre> genres) {
        return new CategoriesUiState(UiStatus.CONTENT, genres, null);
    }

    public static CategoriesUiState empty() {
        return new CategoriesUiState(UiStatus.EMPTY, null, null);
    }

    public static CategoriesUiState error(String message) {
        return new CategoriesUiState(UiStatus.ERROR, null, message);
    }

    public UiStatus getStatus() {
        return status;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public String getMessage() {
        return message;
    }
}