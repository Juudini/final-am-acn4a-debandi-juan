package com.example.parcial_2_am_acn4a_debandi_juan.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public final class AuthService {

    public interface AuthCallback {
        void onSuccess();

        void onError(String message);
    }

    private AuthService() {
    }

    private static FirebaseAuth auth() {
        return FirebaseAuth.getInstance();
    }

    public static boolean isLoggedIn() {
        return auth().getCurrentUser() != null;
    }

    @Nullable
    public static FirebaseUser getCurrentUser() {
        return auth().getCurrentUser();
    }

    @Nullable
    public static String getUid() {
        FirebaseUser user = auth().getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    @Nullable
    public static String getEmail() {
        FirebaseUser user = auth().getCurrentUser();
        return user != null ? user.getEmail() : null;
    }

    public static void signin(@NonNull String email, @NonNull String password, @NonNull AuthCallback callback) {
        auth().signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public static void signup(@NonNull String email, @NonNull String password, @NonNull AuthCallback callback) {
        auth().createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public static void signout() {
        auth().signOut();
    }
}