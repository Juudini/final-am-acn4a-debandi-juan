package com.example.final_am_acn4a_debandi_juan.ui.home;

import com.example.final_am_acn4a_debandi_juan.data.models.Movie;
import com.example.final_am_acn4a_debandi_juan.ui.common.state.UiStatus;

import java.util.Collections;
import java.util.List;

public final class HomeUiState {
    private final UiStatus status;
    private final List<Movie> trending;
    private final List<Movie> newReleases;
    private final boolean heroInWatchlist;
    private final boolean watchlistLoading;
    private final String message;

    public HomeUiState(
        UiStatus status,
        List<Movie> trending,
        List<Movie> newReleases,
        boolean heroInWatchlist,
        boolean watchlistLoading,
        String message
    ) {
        this.status = status;
        this.trending = trending != null ? trending : Collections.emptyList();
        this.newReleases = newReleases != null ? newReleases : Collections.emptyList();
        this.heroInWatchlist = heroInWatchlist;
        this.watchlistLoading = watchlistLoading;
        this.message = message;
    }

    public UiStatus getStatus() {
        return status;
    }

    public List<Movie> getTrending() {
        return trending;
    }

    public List<Movie> getNewReleases() {
        return newReleases;
    }

    public boolean isHeroInWatchlist() {
        return heroInWatchlist;
    }

    public boolean isWatchlistLoading() {
        return watchlistLoading;
    }

    public String getMessage() {
        return message;
    }

    public Movie getHeroMovie() {
        return newReleases.isEmpty() ? null : newReleases.get(0);
    }
}