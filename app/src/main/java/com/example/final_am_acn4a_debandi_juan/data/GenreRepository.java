package com.example.final_am_acn4a_debandi_juan.data;

import com.example.final_am_acn4a_debandi_juan.data.model.Genre;
import com.example.final_am_acn4a_debandi_juan.data.model.GenreResponse;
import com.example.final_am_acn4a_debandi_juan.data.network.RetrofitClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class GenreRepository {
    private static final Map<Integer, String> cache = new HashMap<>();

    private GenreRepository() {
    }

    public static void init(Runnable onComplete) {
        if (!cache.isEmpty()) {
            if (onComplete != null) {
                onComplete.run();
            }
            return;
        }
        RetrofitClient.getApi().getGenres().enqueue(new Callback<GenreResponse>() {
            @Override
            public void onResponse(Call<GenreResponse> call, Response<GenreResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Genre> genres = response.body().getGenres();
                    if (genres != null) {
                        for (Genre g : genres) {
                            cache.put(g.getId(), g.getName());
                        }
                    }
                }
                if (onComplete != null) {
                    onComplete.run();
                }
            }

            @Override
            public void onFailure(Call<GenreResponse> call, Throwable t) {
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        });
    }

    public static String getGenresLabel(List<Integer> genreIds) {
        if (genreIds == null || genreIds.isEmpty()) {
            return "";
        }
        List<String> names = new java.util.ArrayList<>();
        for (Integer id : genreIds) {
            String name = cache.get(id);
            if (name != null) {
                names.add(name);
            }
        }
        if (names.isEmpty()) {
            return "";
        }
        int limit = Math.min(names.size(), 2);
        return android.text.TextUtils.join(", ", names.subList(0, limit));
    }
}
