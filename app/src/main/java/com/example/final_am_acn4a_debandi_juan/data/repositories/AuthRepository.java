package com.example.final_am_acn4a_debandi_juan.data.repositories;

public interface AuthRepository {
    void signIn(String email, String password, DataCallback<Void> callback);
    void signUp(String email, String password, DataCallback<Void> callback);
    void signOut();
    boolean isLoggedIn();
    String getUid();
    String getEmail();
}