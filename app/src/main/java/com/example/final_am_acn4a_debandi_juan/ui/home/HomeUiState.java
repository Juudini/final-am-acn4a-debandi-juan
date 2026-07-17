package com.example.final_am_acn4a_debandi_juan.ui.home;

import com.example.final_am_acn4a_debandi_juan.data.models.Movie;
import com.example.final_am_acn4a_debandi_juan.ui.common.state.UiStatus;

import java.util.Collections;
import java.util.List;

public final class HomeUiState {
    private final UiStatus status;
    private final List<Movie> trending;
    private final List<Movie> newReleases;
    private final String message;

    public HomeUiState(UiStatus status, List<Movie> trending, List<Movie> newReleases, String message) {
        this.status = status;
        this.trending = trending != null ? trending : Collections.emptyList();
        this.newReleases = newReleases != null ? newReleases : Collections.emptyList();
        this.message = message;
    }

    public static HomeUiState loading() {
        return new HomeUiState(UiStatus.LOADING, null, null, null);
    }

    public static HomeUiState content(List<Movie> trending, List<Movie> releases) {
        return new HomeUiState(UiStatus.CONTENT, trending, releases, null);
    }

    public static HomeUiState error(String message) {
        return new HomeUiState(UiStatus.ERROR, null, null, message);
    }

    public UiStatus getStatus() { return status; }
    public List<Movie> getTrending() { return trending; }
    public List<Movie> getNewReleases() { return newReleases; }
    public String getMessage() { return message; }
    public Movie getHeroMovie() {
        return newReleases.isEmpty() ? new Movie() : newReleases.get(0);
    }
}