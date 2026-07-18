package com.example.final_am_acn4a_debandi_juan.ui.auth.signup;

public final class SignupUiState {
    private final boolean loading;
    private final boolean success;
    private final String message;

    private SignupUiState(boolean loading, boolean success, String message) {
        this.loading = loading;
        this.success = success;
        this.message = message;
    }

    public static SignupUiState idle() {
        return new SignupUiState(false, false, null);
    }

    public static SignupUiState loading() {
        return new SignupUiState(true, false, null);
    }

    public static SignupUiState success() {
        return new SignupUiState(false, true, null);
    }

    public static SignupUiState error(String message) {
        return new SignupUiState(false, false, message);
    }

    public boolean isLoading() {
        return loading;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
