package com.example.final_am_acn4a_debandi_juan.ui.genremovies;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.final_am_acn4a_debandi_juan.data.models.Movie;
import com.example.final_am_acn4a_debandi_juan.data.repositories.DataCallback;
import com.example.final_am_acn4a_debandi_juan.data.repositories.MovieRepository;
import com.example.final_am_acn4a_debandi_juan.ui.common.state.UiStatus;

import java.util.ArrayList;
import java.util.List;

public final class GenreMoviesViewModel extends ViewModel {
    private static final String SORT_BY = "popularity.desc";
    private final MovieRepository repository;
    private final int genreId;

    private final MutableLiveData<GenreMoviesUiState> state = new MutableLiveData<>();
    private final List<Movie> accumulated = new ArrayList<>();

    private int page = 1;
    private boolean loading;
    private boolean lastPage;

    public GenreMoviesViewModel(MovieRepository repository, int genreId) {
        this.repository = repository;
        this.genreId = genreId;

        loadNextPage();
    }

    public LiveData<GenreMoviesUiState> getState() {
        return state;
    }

    public void loadNextPage() {
        if (loading || lastPage) {
            return;
        }

        loadPage();
    }

    private void loadPage() {
        loading = true;

        state.setValue(
            new GenreMoviesUiState(
                page == 1 ? UiStatus.LOADING : UiStatus.CONTENT,
                new ArrayList<>(accumulated),
                page > 1,
                lastPage,
                null
            )
        );

        final int requestedPage = page;

        repository.discoverByGenre(genreId, SORT_BY, requestedPage, new DataCallback<List<Movie>>() {
            @Override
            public void onSuccess(List<Movie> movies) {
                loading = false;

                if (movies == null || movies.isEmpty()) {
                    lastPage = true;
                } else {
                    accumulated.addAll(movies);
                    page++;
                }

                UiStatus status = accumulated.isEmpty() ? UiStatus.EMPTY : UiStatus.CONTENT;

                state.setValue(
                    new GenreMoviesUiState(
                        status, new ArrayList<>(accumulated), false, lastPage, null
                    )
                );
            }

            @Override
            public void onError(String message) {
                loading = false;

                UiStatus status = accumulated.isEmpty() ? UiStatus.ERROR : UiStatus.CONTENT;

                state.setValue(
                    new GenreMoviesUiState(
                        status,
                        new ArrayList<>(accumulated),
                        false,
                        lastPage,
                        message
                    )
                );
            }
                }
        );
    }
}