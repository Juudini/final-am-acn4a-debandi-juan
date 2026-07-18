package com.example.final_am_acn4a_debandi_juan.ui.search;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.final_am_acn4a_debandi_juan.data.models.Movie;
import com.example.final_am_acn4a_debandi_juan.data.repositories.DataCallback;
import com.example.final_am_acn4a_debandi_juan.data.repositories.MovieRepository;
import com.example.final_am_acn4a_debandi_juan.ui.common.state.UiStatus;

import java.util.ArrayList;
import java.util.List;

public class SearchViewModel extends ViewModel {
private final MovieRepository repository;
    private final MutableLiveData<SearchUiState> state = new MutableLiveData<>();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final List<Movie> accumulated = new ArrayList<>();

    private Runnable pendingSearch;
    private String query = "";
    private int page = 1;
    private boolean loading;
    private boolean lastPage;

    public SearchViewModel(MovieRepository repository) {
        this.repository = repository;
        state.setValue(new SearchUiState(UiStatus.IDLE, "", accumulated, false, false, null));
    }

    public LiveData<SearchUiState> getState() { return state; }

    public void onQueryChanged(String value) {
        if (pendingSearch != null) {
            handler.removeCallbacks(pendingSearch);
        }

        String normalized = value == null ? "" : value.trim();
        if (normalized.isEmpty()) {
            reset();
            return;
        }
        pendingSearch = () -> searchFirstPage(normalized);
        handler.postDelayed(pendingSearch, 600);
    }

    public void searchNow(String value) {
        if (pendingSearch != null){
            handler.removeCallbacks(pendingSearch);
        }
        searchFirstPage(value == null ? "" : value.trim());
    }

    public void loadNextPage() {
        if (!query.isEmpty() && !loading && !lastPage){
            loadPage();
        }
    }

    private void searchFirstPage(String newQuery) {
        if (newQuery.isEmpty()) {
            reset();
            return;
        }
        query = newQuery;
        page = 1;
        lastPage = false;
        accumulated.clear();
        loadPage();
    }

    private void loadPage() {
        loading = true;
        state.setValue(new SearchUiState(
            page == 1 ? UiStatus.LOADING : UiStatus.CONTENT,
            query,
            new ArrayList<>(accumulated), page > 1, lastPage, null)
        );

        final String requestedQuery = query;
        final int requestedPage = page;

        repository.searchMovies(requestedQuery, requestedPage, new DataCallback<List<Movie>>() {
            @Override
            public void onSuccess(List<Movie> movies) {
                if (!requestedQuery.equals(query)) return;

                loading = false;
                if (movies == null || movies.isEmpty()) {
                    lastPage = true;
                } else {
                    accumulated.addAll(movies);
                    page++;
                }
                UiStatus status = accumulated.isEmpty() ? UiStatus.EMPTY : UiStatus.CONTENT;
                state.setValue(new SearchUiState(status, query, new ArrayList<>(accumulated), false, lastPage, null));
            }

            @Override
            public void onError(String message) {
                if (!requestedQuery.equals(query)) return;
                loading = false;
                state.setValue(
                    new SearchUiState(
                        accumulated.isEmpty() ? UiStatus.ERROR : UiStatus.CONTENT,
                        query, new ArrayList<>(accumulated), false,
                        lastPage, message
                    )
                );
            }
        }
        );
    }

    private void reset() {
        query = "";
        page = 1;
        loading = false;
        lastPage = false;
        accumulated.clear();
        state.setValue(
            new SearchUiState(
                UiStatus.IDLE, "", accumulated, false, false, null
            )
        );
    }

    @Override
    protected void onCleared() {
        if (pendingSearch != null){
            handler.removeCallbacks(pendingSearch);
        }
    }
}
