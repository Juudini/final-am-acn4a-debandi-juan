package com.example.final_am_acn4a_debandi_juan.data.datasources.firebase.auth;

import com.example.final_am_acn4a_debandi_juan.data.repositories.DataCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public final class FirebaseAuthDatasource implements AuthDatasource {

    private final FirebaseAuth auth;

    public FirebaseAuthDatasource(FirebaseAuth auth) {
        this.auth = auth;
    }

    @Override
    public void signIn(String email, String password, DataCallback<Void> callback) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener(result -> callback.onSuccess(null))
            .addOnFailureListener(error -> callback.onError(error.getMessage()));
    }

    @Override
    public void signUp(String email, String password, DataCallback<Void> callback) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener(result -> callback.onSuccess(null))
            .addOnFailureListener(error -> callback.onError(error.getMessage()));
    }

    @Override
    public void signOut() {
        auth.signOut();
    }

    @Override
    public boolean isLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    @Override
    public String getUid() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    @Override
    public String getEmail() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null ? user.getEmail() : null;
    }
}