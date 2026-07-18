package com.example.final_am_acn4a_debandi_juan.ui.search;

import com.example.final_am_acn4a_debandi_juan.data.models.Movie;
import com.example.final_am_acn4a_debandi_juan.ui.common.state.UiStatus;

import java.util.Collections;
import java.util.List;

public class SearchUiState {
    private final UiStatus status;
    private final String query;
    private final List<Movie> movies;
    private final boolean loadingNextPage;
    private final boolean lastPage;
    private final String message;

    public SearchUiState(UiStatus status, String query, List<Movie> movies, boolean loadingNextPage, boolean lastPage, String message) {
        this.status = status;
        this.query = query;
        this.movies = movies != null ? movies : Collections.emptyList();
        this.loadingNextPage = loadingNextPage;
        this.lastPage = lastPage;
        this.message = message;
    }

    public UiStatus getStatus() { return status; }
    public String getQuery() { return query; }
    public List<Movie> getMovies() { return movies; }
    public boolean isLoadingNextPage() { return loadingNextPage; }
    public boolean isLastPage() { return lastPage; }
    public String getMessage() { return message; }
}
