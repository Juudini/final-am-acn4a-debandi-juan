package com.example.final_am_acn4a_debandi_juan.ui.auth.signup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.final_am_acn4a_debandi_juan.data.repositories.AuthRepository;
import com.example.final_am_acn4a_debandi_juan.data.repositories.DataCallback;

public final class SignupViewModel extends ViewModel {
    private static final String EMPTY_FIELDS_ERROR = "Complete all fields";
    private static final String PASSWORD_MISMATCH_ERROR = "Passwords do not match";
    private static final String DEFAULT_SIGNUP_ERROR = "Could not create account";

    private final AuthRepository repository;
    private final MutableLiveData<SignupUiState> state = new MutableLiveData<>(SignupUiState.idle());

    public SignupViewModel(AuthRepository repository) {
        this.repository = repository;
    }

    public LiveData<SignupUiState> getState() {
        return state;
    }

    public void signUp(String email, String password, String confirmPassword) {
        SignupUiState currentState = state.getValue();
        if (currentState != null && currentState.isLoading()) {
            return;
        }

        String normalizedEmail = email == null ? "" : email.trim();
        String safePassword = password == null ? "" : password;
        String safeConfirmPassword = confirmPassword == null ? "" : confirmPassword;

        if (normalizedEmail.isEmpty() || safePassword.isEmpty() || safeConfirmPassword.isEmpty()) {
            state.setValue(SignupUiState.error(EMPTY_FIELDS_ERROR));
            return;
        }
        if (!safePassword.equals(safeConfirmPassword)) {
            state.setValue(SignupUiState.error(PASSWORD_MISMATCH_ERROR));
            return;
        }

        state.setValue(SignupUiState.loading());
        repository.signUp(normalizedEmail, safePassword, new DataCallback<Void>() {
            @Override
            public void onSuccess(Void ignored) {
                state.setValue(SignupUiState.success());
            }

            @Override
            public void onError(String message) {
                state.setValue(
                    SignupUiState.error(
                        message == null || message.trim().isEmpty() ? DEFAULT_SIGNUP_ERROR : message
                    )
                );
            }
        });
    }
}
