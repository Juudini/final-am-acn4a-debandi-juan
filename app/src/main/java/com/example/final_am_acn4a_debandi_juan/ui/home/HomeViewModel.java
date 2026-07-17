package com.example.final_am_acn4a_debandi_juan.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.final_am_acn4a_debandi_juan.data.models.Movie;
import com.example.final_am_acn4a_debandi_juan.data.repositories.DataCallback;
import com.example.final_am_acn4a_debandi_juan.data.repositories.MovieRepository;

import java.util.Collections;
import java.util.List;

public final class HomeViewModel extends ViewModel {
    private final MovieRepository movieRepository;
    private final MutableLiveData<HomeUiState> state = new MutableLiveData<>();

    private List<Movie> trending = Collections.emptyList();
    private List<Movie> releases = Collections.emptyList();
    private int pendingRequests;
    private boolean failed;

    public HomeViewModel(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
        load();
    }

    public LiveData<HomeUiState> getState() {
        return state;
    }

    public void load() {
        pendingRequests = 2;
        failed = false;
        state.setValue(HomeUiState.loading());

        movieRepository.getTrending(callback(true));
        movieRepository.getNowPlaying(1, callback(false));
    }

    private DataCallback<List<Movie>> callback(boolean isTrending) {
        return new DataCallback<List<Movie>>() {
            @Override
            public void onSuccess(List<Movie> movies) {
                if(isTrending) {
                    trending = movies;
                }
                else {
                    releases = movies;
                }

                finishRequest();
            }

            @Override
            public void onError(String message) {
                failed = true;
                finishRequest();
            }
        };
    }

    private void finishRequest() {
        pendingRequests--;
        if (pendingRequests > 0) return;

        if (failed) {
            state.postValue(HomeUiState.error("Couldn't load the home"));
        }else{
            state.setValue(HomeUiState.content(trending, releases));
        }
    }
}