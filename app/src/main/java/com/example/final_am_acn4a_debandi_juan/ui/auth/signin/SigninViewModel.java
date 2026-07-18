package com.example.final_am_acn4a_debandi_juan.ui.auth.signin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.final_am_acn4a_debandi_juan.data.repositories.AuthRepository;
import com.example.final_am_acn4a_debandi_juan.data.repositories.DataCallback;

public final class SigninViewModel extends ViewModel {
    private static final String EMPTY_FIELDS_ERROR = "Enter email and password";
    private static final String DEFAULT_AUTH_ERROR = "Authentication failed";

    private final AuthRepository repository;
    private final MutableLiveData<SigninUiState> state = new MutableLiveData<>(SigninUiState.idle());
    public SigninViewModel(AuthRepository repository) {
        this.repository = repository;
    }
    public LiveData<SigninUiState> getState() {
        return state;
    }

    public void signIn(String email, String password) {
        SigninUiState currentState = state.getValue();
        if (currentState != null && currentState.isLoading()) {
            return;
        }

        String normalizedEmail = email == null ? "" : email.trim();
        String safePassword = password == null ? "" : password;

        if (normalizedEmail.isEmpty() || safePassword.isEmpty()) {
            state.setValue(SigninUiState.error(EMPTY_FIELDS_ERROR));
            return;
        }

        state.setValue(SigninUiState.loading());
        repository.signIn(normalizedEmail, safePassword, new DataCallback<Void>() {
            @Override
            public void onSuccess(Void ignored) {
                state.setValue(SigninUiState.success());
            }

            @Override
            public void onError(String message) {
                state.setValue(
                    SigninUiState.error(
                        message == null || message.trim().isEmpty() ? DEFAULT_AUTH_ERROR : message
                    )
                );
            }
        });
    }
}
