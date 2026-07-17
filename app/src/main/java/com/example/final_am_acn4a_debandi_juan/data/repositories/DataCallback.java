package com.example.final_am_acn4a_debandi_juan.data.repositories;

public interface DataCallback<T> {
    void onSuccess(T data);
    void onError(String message);
}
