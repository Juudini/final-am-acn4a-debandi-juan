package com.example.final_am_acn4a_debandi_juan.ui.moviedetail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.final_am_acn4a_debandi_juan.data.models.CastMember;
import com.example.final_am_acn4a_debandi_juan.data.models.Genre;
import com.example.final_am_acn4a_debandi_juan.data.models.Movie;
import com.example.final_am_acn4a_debandi_juan.data.models.MovieDetail;
import com.example.final_am_acn4a_debandi_juan.data.repositories.AuthRepository;
import com.example.final_am_acn4a_debandi_juan.data.repositories.DataCallback;
import com.example.final_am_acn4a_debandi_juan.data.repositories.MovieRepository;
import com.example.final_am_acn4a_debandi_juan.data.repositories.WatchlistRepository;
import com.example.final_am_acn4a_debandi_juan.ui.common.state.UiStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MovieDetailViewModel extends ViewModel {
    public enum Event {
        NAVIGATE_TO_SIGNIN,
        ADDED_TO_WATCHLIST,
        REMOVED_FROM_WATCHLIST,
        WATCHLIST_ERROR
    }

    private final MovieRepository movieRepository;
    private final WatchlistRepository watchlistRepository;
    private final AuthRepository authRepository;
    private final int movieId;
    private final MutableLiveData<MovieDetailUiState> state = new MutableLiveData<>();
    private final MutableLiveData<Event> event = new MutableLiveData<>();

    private MovieDetail detail;
    private List<CastMember> cast = Collections.emptyList();
    private boolean creditsLoading = true;
    private boolean inWatchlist;
    private boolean watchlistLoading;
    private String detailError;

    public MovieDetailViewModel(
        MovieRepository movieRepository, WatchlistRepository watchlistRepository, AuthRepository authRepository, int movieId) {
        this.movieRepository = movieRepository;
        this.watchlistRepository = watchlistRepository;
        this.authRepository = authRepository;
        this.movieId = movieId;
        load();
    }

    public LiveData<MovieDetailUiState> getState() {
        return state;
    }

    public LiveData<Event> getEvent() {
        return event;
    }

    public void consumeEvent() {
        event.setValue(null);
    }

    public void refreshWatchlist() {
        if (!authRepository.isLoggedIn()) {
            inWatchlist = false;
            watchlistLoading = false;
            publish();
            return;
        }

        watchlistLoading = true;
        publish();
        watchlistRepository.contains(movieId, new DataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                inWatchlist = Boolean.TRUE.equals(result);
                watchlistLoading = false;
                publish();
            }

            @Override
            public void onError(String message) {
                inWatchlist = false;
                watchlistLoading = false;
                publish();
            }
        });
    }

    public void toggleWatchlist() {
        if (!authRepository.isLoggedIn()) {
            event.setValue(Event.NAVIGATE_TO_SIGNIN);
            return;
        }
        if (detail == null || watchlistLoading) {
            return;
        }

        watchlistLoading = true;
        publish();
        if (inWatchlist) {
            removeFromWatchlist();
        } else {
            addToWatchlist();
        }
    }

    private void load() {
        publish();
        movieRepository.getMovieDetail(movieId, new DataCallback<MovieDetail>() {
            @Override
            public void onSuccess(MovieDetail result) {
                detail = result;
                detailError = null;
                publish();
            }

            @Override
            public void onError(String message) {
                detailError = message;
                publish();
            }
        });

        movieRepository.getMovieCredits(movieId, new DataCallback<List<CastMember>>() {
            @Override
            public void onSuccess(List<CastMember> result) {
                cast = result != null ? result : Collections.emptyList();
                creditsLoading = false;
                publish();
            }

            @Override
            public void onError(String message) {
                cast = Collections.emptyList();
                creditsLoading = false;
                publish();
            }
        });
    }

    private void addToWatchlist() {
        watchlistRepository.add(toMovie(detail), new DataCallback<Void>() {
            @Override
            public void onSuccess(Void ignored) {
                inWatchlist = true;
                watchlistLoading = false;
                publish();
                event.setValue(Event.ADDED_TO_WATCHLIST);
            }

            @Override
            public void onError(String message) {
                watchlistLoading = false;
                publish();
                event.setValue(Event.WATCHLIST_ERROR);
            }
        });
    }

    private void removeFromWatchlist() {
        watchlistRepository.remove(movieId, new DataCallback<Void>() {
            @Override
            public void onSuccess(Void ignored) {
                inWatchlist = false;
                watchlistLoading = false;
                publish();
                event.setValue(Event.REMOVED_FROM_WATCHLIST);
            }

            @Override
            public void onError(String message) {
                watchlistLoading = false;
                publish();
                event.setValue(Event.WATCHLIST_ERROR);
            }
        });
    }

    private Movie toMovie(MovieDetail detail) {
        Movie movie = new Movie(
            detail.getId(),
            detail.getTitle(),
            detail.getOverview(),
            detail.getPosterPath(),
            detail.getBackdropPath(),
            detail.getVoteAverage(),
            detail.getReleaseDate()
        );
        List<Integer> genreIds = new ArrayList<>();
        if (detail.getGenres() != null) {
            for (Genre genre : detail.getGenres()) {
                genreIds.add(genre.getId());
            }
        }
        movie.setGenreIds(genreIds);
        return movie;
    }

    private void publish() {
        UiStatus status;
        if (detail != null) {
            status = UiStatus.CONTENT;
        } else if (detailError != null) {
            status = UiStatus.ERROR;
        } else {
            status = UiStatus.LOADING;
        }
        state.setValue(
            new MovieDetailUiState(
                status,
                detail,
                cast,
                creditsLoading,
                inWatchlist,
                watchlistLoading,
                detailError
            )
        );
    }
}

