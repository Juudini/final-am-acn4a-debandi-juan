package com.example.final_am_acn4a_debandi_juan.ui.account;

public final class AccountUiState {
    private final boolean loggedIn;
    private final String email;

    private AccountUiState(boolean loggedIn, String email) {
        this.loggedIn = loggedIn;
        this.email = email;
    }

    public static AccountUiState loggedIn(String email) {
        return new AccountUiState(true, email);
    }

    public static AccountUiState loggedOut() {
        return new AccountUiState(false, null);
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public String getEmail() {
        return email;
    }
}
