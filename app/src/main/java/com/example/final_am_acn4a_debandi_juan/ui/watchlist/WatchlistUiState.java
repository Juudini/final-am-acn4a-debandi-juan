package com.example.final_am_acn4a_debandi_juan.ui.watchlist;

import com.example.final_am_acn4a_debandi_juan.data.models.Movie;
import com.example.final_am_acn4a_debandi_juan.ui.common.state.UiStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class WatchlistUiState {
    private final UiStatus status;
    private final List<Movie> movies;
    private final Set<Integer> pendingRemovalIds;
    private final String message;
    private final boolean authenticationRequired;

    private WatchlistUiState(UiStatus status, List<Movie> movies, Set<Integer> pendingRemovalIds, String message, boolean authenticationRequired) {
        this.status = status;
        this.movies = Collections.unmodifiableList(movies != null ? new ArrayList<>(movies) : new ArrayList<>());
        this.pendingRemovalIds = Collections.unmodifiableSet(
            pendingRemovalIds != null ? new HashSet<>(pendingRemovalIds) : new HashSet<>()
        );
        this.message = message;
        this.authenticationRequired = authenticationRequired;
    }

    public static WatchlistUiState loading() {
        return new WatchlistUiState(UiStatus.LOADING, null, null, null, false);
    }

    public static WatchlistUiState display(List<Movie> movies, Set<Integer> pendingRemovalIds, String message) {
        UiStatus status = movies == null || movies.isEmpty() ? UiStatus.EMPTY : UiStatus.CONTENT;
        return new WatchlistUiState(status, movies, pendingRemovalIds, message, false);
    }

    public static WatchlistUiState error(String message) {
        return new WatchlistUiState(UiStatus.ERROR, null, null, message, false);
    }

    public static WatchlistUiState authenticationRequired() {
        return new WatchlistUiState(UiStatus.IDLE, null, null, null, true);
    }

    public UiStatus getStatus() {
        return status;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public Set<Integer> getPendingRemovalIds() {
        return pendingRemovalIds;
    }

    public String getMessage() {
        return message;
    }

    public boolean isAuthenticationRequired() {
        return authenticationRequired;
    }
}