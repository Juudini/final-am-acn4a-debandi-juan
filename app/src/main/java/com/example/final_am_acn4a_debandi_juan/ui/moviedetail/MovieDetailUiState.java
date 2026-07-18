package com.example.final_am_acn4a_debandi_juan.ui.moviedetail;

import com.example.final_am_acn4a_debandi_juan.data.models.CastMember;
import com.example.final_am_acn4a_debandi_juan.data.models.MovieDetail;
import com.example.final_am_acn4a_debandi_juan.ui.common.state.UiStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MovieDetailUiState {
    private final UiStatus status;
    private final MovieDetail detail;
    private final List<CastMember> cast;
    private final boolean creditsLoading;
    private final boolean inWatchlist;
    private final boolean watchlistLoading;
    private final String message;

    public MovieDetailUiState(UiStatus status, MovieDetail detail,
                              List<CastMember> cast, boolean creditsLoading,
                              boolean inWatchlist, boolean watchlistLoading,
                              String message) {
        this.status = status;
        this.detail = detail;
        this.cast = Collections.unmodifiableList(
                cast != null ? new ArrayList<>(cast) : new ArrayList<>());
        this.creditsLoading = creditsLoading;
        this.inWatchlist = inWatchlist;
        this.watchlistLoading = watchlistLoading;
        this.message = message;
    }

    public UiStatus getStatus() {
        return status;
    }

    public MovieDetail getDetail() {
        return detail;
    }

    public List<CastMember> getCast() {
        return cast;
    }

    public boolean isCreditsLoading() {
        return creditsLoading;
    }

    public boolean isInWatchlist() {
        return inWatchlist;
    }

    public boolean isWatchlistLoading() {
        return watchlistLoading;
    }

    public String getMessage() {
        return message;
    }
}

