package com.example.final_am_acn4a_debandi_juan.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.final_am_acn4a_debandi_juan.data.models.Movie;
import com.example.final_am_acn4a_debandi_juan.data.repositories.AuthRepository;
import com.example.final_am_acn4a_debandi_juan.data.repositories.DataCallback;
import com.example.final_am_acn4a_debandi_juan.data.repositories.MovieRepository;
import com.example.final_am_acn4a_debandi_juan.data.repositories.WatchlistRepository;
import com.example.final_am_acn4a_debandi_juan.ui.common.state.UiStatus;

import java.util.Collections;
import java.util.List;

public final class HomeViewModel extends ViewModel {
    public enum Event {
        NAVIGATE_TO_SIGNIN,
        ADDED_TO_WATCHLIST,
        REMOVED_FROM_WATCHLIST,
        WATCHLIST_ERROR
    }

    private static final String HOME_ERROR = "Couldn't load the home";

    private final MovieRepository movieRepository;
    private final WatchlistRepository watchlistRepository;
    private final AuthRepository authRepository;
    private final MutableLiveData<HomeUiState> state = new MutableLiveData<>();
    private final MutableLiveData<Event> event = new MutableLiveData<>();

    private List<Movie> trending = Collections.emptyList();
    private List<Movie> releases = Collections.emptyList();
    private UiStatus status = UiStatus.LOADING;
    private String message;
    private int pendingRequests;
    private boolean failed;
    private boolean heroInWatchlist;
    private boolean watchlistLoading;

    public HomeViewModel(
        MovieRepository movieRepository,
        WatchlistRepository watchlistRepository,
        AuthRepository authRepository
    ) {
        this.movieRepository = movieRepository;
        this.watchlistRepository = watchlistRepository;
        this.authRepository = authRepository;
        load();
    }

    public LiveData<HomeUiState> getState() {
        return state;
    }

    public LiveData<Event> getEvent() {
        return event;
    }

    public void consumeEvent() {
        event.setValue(null);
    }

    public void load() {
        pendingRequests = 2;
        failed = false;
        status = UiStatus.LOADING;
        message = null;
        heroInWatchlist = false;
        watchlistLoading = false;
        publish();

        movieRepository.getTrending(callback(true));
        movieRepository.getNowPlaying(1, callback(false));
    }

    public void refreshHeroWatchlist() {
        Movie hero = getHeroMovie();
        if (hero == null) {
            return;
        }

        if (!authRepository.isLoggedIn()) {
            heroInWatchlist = false;
            watchlistLoading = false;
            publish();
            return;
        }

        watchlistLoading = true;
        publish();
        watchlistRepository.contains(hero.getId(), new DataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                heroInWatchlist = Boolean.TRUE.equals(result);
                watchlistLoading = false;
                publish();
            }

            @Override
            public void onError(String errorMessage) {
                heroInWatchlist = false;
                watchlistLoading = false;
                publish();
            }
        });
    }

    public void toggleHeroWatchlist() {
        if (!authRepository.isLoggedIn()) {
            event.setValue(Event.NAVIGATE_TO_SIGNIN);
            return;
        }

        Movie hero = getHeroMovie();
        if (hero == null || watchlistLoading) {
            return;
        }

        watchlistLoading = true;
        publish();
        if (heroInWatchlist) {
            removeHero(hero.getId());
        } else {
            addHero(hero);
        }
    }

    private DataCallback<List<Movie>> callback(boolean isTrending) {
        return new DataCallback<List<Movie>>() {
            @Override
            public void onSuccess(List<Movie> movies) {
                List<Movie> result = movies != null ? movies : Collections.emptyList();
                if (isTrending) {
                    trending = result;
                } else {
                    releases = result;
                }
                finishRequest();
            }

            @Override
            public void onError(String errorMessage) {
                failed = true;
                finishRequest();
            }
        };
    }

    private void finishRequest() {
        pendingRequests--;
        if (pendingRequests > 0) {
            return;
        }

        if (failed) {
            status = UiStatus.ERROR;
            message = HOME_ERROR;
            publish();
            return;
        }

        status = UiStatus.CONTENT;
        message = null;
        publish();
        refreshHeroWatchlist();
    }

    private void addHero(Movie hero) {
        watchlistRepository.add(hero, new DataCallback<Void>() {
            @Override
            public void onSuccess(Void ignored) {
                heroInWatchlist = true;
                watchlistLoading = false;
                publish();
                event.setValue(Event.ADDED_TO_WATCHLIST);
            }

            @Override
            public void onError(String errorMessage) {
                watchlistLoading = false;
                publish();
                event.setValue(Event.WATCHLIST_ERROR);
            }
        });
    }

    private void removeHero(int movieId) {
        watchlistRepository.remove(movieId, new DataCallback<Void>() {
            @Override
            public void onSuccess(Void ignored) {
                heroInWatchlist = false;
                watchlistLoading = false;
                publish();
                event.setValue(Event.REMOVED_FROM_WATCHLIST);
            }

            @Override
            public void onError(String errorMessage) {
                watchlistLoading = false;
                publish();
                event.setValue(Event.WATCHLIST_ERROR);
            }
        });
    }

    private Movie getHeroMovie() {
        return releases.isEmpty() ? null : releases.get(0);
    }

    private void publish() {
        state.setValue(
            new HomeUiState(status, trending, releases, heroInWatchlist, watchlistLoading, message)
        );
    }
}
