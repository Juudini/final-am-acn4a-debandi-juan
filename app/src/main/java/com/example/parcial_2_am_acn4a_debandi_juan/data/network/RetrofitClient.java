package com.example.parcial_2_am_acn4a_debandi_juan.data.network;

import androidx.annotation.NonNull;

import com.example.parcial_2_am_acn4a_debandi_juan.BuildConfig;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class RetrofitClient {

    private static final String DEFAULT_LANGUAGE = "en-US";

    private static volatile TmdbApi api;

    private RetrofitClient() {
    }

    public static TmdbApi getApi() {
        if (api == null) {
            synchronized (RetrofitClient.class) {
                if (api == null) {
                    api = buildApi();
                }
            }
        }
        return api;
    }

    private static TmdbApi buildApi() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(BuildConfig.DEBUG
                ? HttpLoggingInterceptor.Level.BODY
                : HttpLoggingInterceptor.Level.NONE);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new ApiKeyInterceptor())
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.TMDB_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(TmdbApi.class);
    }

    private static class ApiKeyInterceptor implements Interceptor {
        @NonNull
        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            Request original = chain.request();
            HttpUrl url = original.url().newBuilder()
                    .addQueryParameter("language", DEFAULT_LANGUAGE)
                    .build();
            Request request = original.newBuilder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + BuildConfig.TMDB_API_KEY)
                    .addHeader("accept", "application/json")
                    .build();
            return chain.proceed(request);
        }
    }
}
