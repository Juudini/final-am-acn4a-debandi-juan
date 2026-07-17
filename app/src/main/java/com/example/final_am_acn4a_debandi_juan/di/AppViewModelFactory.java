package com.example.final_am_acn4a_debandi_juan.di;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.final_am_acn4a_debandi_juan.ui.home.HomeViewModel;

public final class AppViewModelFactory implements ViewModelProvider.Factory {
    private final AppModule appModule;

    public AppViewModelFactory(AppModule appModule) {
        this.appModule = appModule;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == HomeViewModel.class) {
            return (T) new HomeViewModel(appModule.getMovieRepository());
        }
        throw new IllegalArgumentException("Unknown ViewModel: " + modelClass.getName());
    }
}