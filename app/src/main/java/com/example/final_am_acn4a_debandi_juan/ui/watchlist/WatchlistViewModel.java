package com.example.final_am_acn4a_debandi_juan.ui.watchlist;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.final_am_acn4a_debandi_juan.data.models.Movie;
import com.example.final_am_acn4a_debandi_juan.data.repositories.AuthRepository;
import com.example.final_am_acn4a_debandi_juan.data.repositories.DataCallback;
import com.example.final_am_acn4a_debandi_juan.data.repositories.WatchlistRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public final class WatchlistViewModel extends ViewModel {
    private static final long UNDO_DELAY_MS = 3500L;
    private static final String REMOVE_ERROR = "Couldn't delete the movie";

    private final WatchlistRepository repository;
    private final AuthRepository authRepository;
    private final MutableLiveData<WatchlistUiState> state = new MutableLiveData<>();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final List<Movie> movies = new ArrayList<>();
    private final Map<Integer, PendingRemoval> pendingRemovals = new HashMap<>();

    public WatchlistViewModel(WatchlistRepository repository, AuthRepository authRepository) {
        this.repository = repository;
        this.authRepository = authRepository;
    }

    public LiveData<WatchlistUiState> getState() {
        return state;
    }

    public void load() {
        if (!authRepository.isLoggedIn()) {
            state.setValue(WatchlistUiState.authenticationRequired());
            return;
        }

        state.setValue(WatchlistUiState.loading());
        repository.getAll(new DataCallback<List<Movie>>() {
            @Override
            public void onSuccess(List<Movie> result) {
                movies.clear();
                if (result != null) {
                    for (Movie movie : result) {
                        if (!pendingRemovals.containsKey(movie.getId())) {
                            movies.add(movie);
                        }
                    }
                }
                publish(null);
            }

            @Override
            public void onError(String message) {
                if (!authRepository.isLoggedIn()) {
                    state.setValue(WatchlistUiState.authenticationRequired());
                } else {
                    state.setValue(WatchlistUiState.error(message));
                }
            }
        });
    }

    public void requestRemove(Movie movie) {
        if (movie == null || pendingRemovals.containsKey(movie.getId())) {
            return;
        }

        int index = indexOf(movie.getId());
        if (index < 0) {
            return;
        }

        Movie removedMovie = movies.remove(index);
        Runnable confirmation = () -> confirmRemove(removedMovie.getId());
        pendingRemovals.put(
            removedMovie.getId(),
            new PendingRemoval(removedMovie, index, confirmation)
        );
        handler.postDelayed(confirmation, UNDO_DELAY_MS);
        publish(null);
    }

    public void undoRemove(Movie movie) {
        if (movie == null) {
            return;
        }

        PendingRemoval pending = pendingRemovals.remove(movie.getId());
        if (pending == null) {
            return;
        }

        handler.removeCallbacks(pending.confirmation);
        restore(pending);
        publish(null);
    }

    public void confirmRemove(int movieId) {
        PendingRemoval pending = pendingRemovals.remove(movieId);
        if (pending == null) {
            return;
        }

        handler.removeCallbacks(pending.confirmation);
        repository.remove(movieId, new DataCallback<Void>() {
            @Override
            public void onSuccess(Void ignored) {
                publish(null);
            }

            @Override
            public void onError(String message) {
                restore(pending);
                publish(message == null || message.trim().isEmpty() ? REMOVE_ERROR : message);
            }
        });
    }

    public void flushPendingRemovals() {
        List<Integer> movieIds = new ArrayList<>(pendingRemovals.keySet());
        for (Integer movieId : movieIds) {
            PendingRemoval pending = pendingRemovals.get(movieId);
            if (pending != null) {
                handler.removeCallbacks(pending.confirmation);
            }
            confirmRemove(movieId);
        }
    }

    private void restore(PendingRemoval pending) {
        if (indexOf(pending.movie.getId()) >= 0) {
            return;
        }
        int index = Math.min(pending.index, movies.size());
        movies.add(index, pending.movie);
    }

    private int indexOf(int movieId) {
        for (int index = 0; index < movies.size(); index++) {
            if (movies.get(index).getId() == movieId) {
                return index;
            }
        }
        return -1;
    }

    private void publish(String message) {
        state.setValue(WatchlistUiState.display(
            movies,
            new HashSet<>(pendingRemovals.keySet()),
            message)
        );
    }

    @Override
    protected void onCleared() {
        flushPendingRemovals();
        handler.removeCallbacksAndMessages(null);
    }

    private static final class PendingRemoval {
        private final Movie movie;
        private final int index;
        private final Runnable confirmation;

        private PendingRemoval(Movie movie, int index, Runnable confirmation) {
            this.movie = movie;
            this.index = index;
            this.confirmation = confirmation;
        }
    }
}

