package com.example.final_am_acn4a_debandi_juan.ui.auth.signin;

public class SigninUiState {
    private final boolean loading;
    private final boolean success;
    private final String message;

    private SigninUiState(boolean loading, boolean success, String message) {
        this.loading = loading;
        this.success = success;
        this.message = message;
    }

    public static SigninUiState idle() {
        return new SigninUiState(false, false, null);
    }

    public static SigninUiState loading() {
        return new SigninUiState(true, false, null);
    }

    public static SigninUiState success() {
        return new SigninUiState(false, true, null);
    }

    public static SigninUiState error(String message) {
        return new SigninUiState(false, false, message);
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
