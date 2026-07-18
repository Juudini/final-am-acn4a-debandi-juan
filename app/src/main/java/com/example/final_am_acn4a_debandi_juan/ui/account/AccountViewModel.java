package com.example.final_am_acn4a_debandi_juan.ui.account;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.final_am_acn4a_debandi_juan.data.repositories.AuthRepository;

public final class AccountViewModel extends ViewModel {
    private final AuthRepository repository;
    private final MutableLiveData<AccountUiState> state = new MutableLiveData<>();

    public AccountViewModel(AuthRepository repository) {
        this.repository = repository;
        refresh();
    }

    public LiveData<AccountUiState> getState() {
        return state;
    }

    public void refresh() {
        if (repository.isLoggedIn()) {
            state.setValue(AccountUiState.loggedIn(repository.getEmail()));
        } else {
            state.setValue(AccountUiState.loggedOut());
        }
    }

    public void signOut() {
        repository.signOut();
        state.setValue(AccountUiState.loggedOut());
    }
}
