package com.example.final_am_acn4a_debandi_juan.data;

import com.example.final_am_acn4a_debandi_juan.data.datasources.network.RetrofitClient;
import com.example.final_am_acn4a_debandi_juan.data.datasources.network.dtos.GenreResponseDto;
import com.example.final_am_acn4a_debandi_juan.data.mappers.TmdbGenreMapper;
import com.example.final_am_acn4a_debandi_juan.data.models.Genre;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class GenreRepository {
    private static final Map<Integer, String> cache = new HashMap<>();
    private static final TmdbGenreMapper mapper = new TmdbGenreMapper();

    private GenreRepository() {
    }

    public static void init(Runnable onComplete) {
        if (!cache.isEmpty()) {
            if (onComplete != null) {
                onComplete.run();
            }
            return;
        }
        RetrofitClient.getApi().getGenres().enqueue(new Callback<GenreResponseDto>() {
            @Override
            public void onResponse(Call<GenreResponseDto> call,
                                   Response<GenreResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Genre> genres = mapper.toModels(response.body().getGenres());
                    for (Genre genre : genres) {
                        cache.put(genre.getId(), genre.getName());
                    }
                }
                complete(onComplete);
            }

            @Override
            public void onFailure(Call<GenreResponseDto> call, Throwable error) {
                complete(onComplete);
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

    private static void complete(Runnable onComplete) {
        if (onComplete != null) {
            onComplete.run();
        }
    }
}
