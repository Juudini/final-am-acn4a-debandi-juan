package com.example.final_am_acn4a_debandi_juan.data.repositories.impl;

import com.example.final_am_acn4a_debandi_juan.data.datasources.firebase.auth.AuthDatasource;
import com.example.final_am_acn4a_debandi_juan.data.repositories.AuthRepository;
import com.example.final_am_acn4a_debandi_juan.data.repositories.DataCallback;

public final class AuthRepositoryImpl implements AuthRepository {

    private final AuthDatasource dataSource;

    public AuthRepositoryImpl(AuthDatasource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void signIn(String email, String password, DataCallback<Void> callback) {
        dataSource.signIn(email, password, callback);
    }

    @Override
    public void signUp(String email, String password, DataCallback<Void> callback) {
        dataSource.signUp(email, password, callback);
    }

    @Override
    public void signOut() {
        dataSource.signOut();
    }

    @Override
    public boolean isLoggedIn() {
        return dataSource.isLoggedIn();
    }

    @Override
    public String getUid() {
        return dataSource.getUid();
    }

    @Override
    public String getEmail() {
        return dataSource.getEmail();
    }
}