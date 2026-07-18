package com.example.final_am_acn4a_debandi_juan.ui.categories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.final_am_acn4a_debandi_juan.data.models.Genre;
import com.example.final_am_acn4a_debandi_juan.data.repositories.DataCallback;
import com.example.final_am_acn4a_debandi_juan.data.repositories.GenreRepository;

import java.util.List;

public final class CategoriesViewModel extends ViewModel {
    private final GenreRepository genreRepository;
    private final MutableLiveData<CategoriesUiState> state = new MutableLiveData<>();

    public CategoriesViewModel(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
        load();
    }

    public LiveData<CategoriesUiState> getState() {
        return state;
    }

    public void load() {
        state.setValue(CategoriesUiState.loading());

        genreRepository.getGenres(
                new DataCallback<List<Genre>>() {
                    @Override
                    public void onSuccess(List<Genre> genres) {
                        if (genres == null || genres.isEmpty()) {
                            state.setValue(CategoriesUiState.empty());
                        } else {
                            state.setValue(CategoriesUiState.content(genres));
                        }
                    }

                    @Override
                    public void onError(String message) {
                        state.setValue(CategoriesUiState.error(message));
                    }
                }
        );
    }
}