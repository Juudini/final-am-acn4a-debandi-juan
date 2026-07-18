package com.example.final_am_acn4a_debandi_juan.data.datasources.firebase.auth;

import com.example.final_am_acn4a_debandi_juan.data.repositories.DataCallback;

public interface AuthDatasource {
    void signIn(String email, String password, DataCallback<Void> callback);
    void signUp(String email, String password, DataCallback<Void> callback);
    void signOut();
    boolean isLoggedIn();
    String getUid();
    String getEmail();
}
