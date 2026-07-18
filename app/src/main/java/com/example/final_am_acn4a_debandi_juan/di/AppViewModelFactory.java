package com.example.final_am_acn4a_debandi_juan.di;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.final_am_acn4a_debandi_juan.ui.account.AccountViewModel;
import com.example.final_am_acn4a_debandi_juan.ui.auth.signin.SigninViewModel;
import com.example.final_am_acn4a_debandi_juan.ui.auth.signup.SignupViewModel;
import com.example.final_am_acn4a_debandi_juan.ui.categories.CategoriesViewModel;
import com.example.final_am_acn4a_debandi_juan.ui.home.HomeViewModel;
import com.example.final_am_acn4a_debandi_juan.ui.newreleases.NewReleasesViewModel;
import com.example.final_am_acn4a_debandi_juan.ui.search.SearchViewModel;
import com.example.final_am_acn4a_debandi_juan.ui.watchlist.WatchlistViewModel;

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
            return (T) new HomeViewModel(
                appModule.getMovieRepository(),
                appModule.getWatchlistRepository(),
                appModule.getAuthRepository()
            );
        }

        if (modelClass == SearchViewModel.class) {
            return (T) new SearchViewModel(appModule.getMovieRepository());
        }

        if (modelClass == CategoriesViewModel.class) {
            return (T) new CategoriesViewModel(appModule.getGenreRepository());
        }

        if (modelClass == NewReleasesViewModel.class) {
            return (T) new NewReleasesViewModel(appModule.getMovieRepository());
        }

        if (modelClass == SigninViewModel.class) {
            return (T) new SigninViewModel(appModule.getAuthRepository());
        }

        if (modelClass == SignupViewModel.class) {
            return (T) new SignupViewModel(appModule.getAuthRepository());
        }

        if (modelClass == AccountViewModel.class) {
            return (T) new AccountViewModel(appModule.getAuthRepository());
        }

        if (modelClass == WatchlistViewModel.class) {
            return (T) new WatchlistViewModel(
                appModule.getWatchlistRepository(),
                appModule.getAuthRepository()
            );
        }

        throw new IllegalArgumentException("Unknown ViewModel: " + modelClass.getName());
    }
}
