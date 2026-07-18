package com.example.final_am_acn4a_debandi_juan.di;

import com.example.final_am_acn4a_debandi_juan.data.datasources.firebase.auth.AuthDatasource;
import com.example.final_am_acn4a_debandi_juan.data.datasources.firebase.auth.FirebaseAuthDatasource;
import com.example.final_am_acn4a_debandi_juan.data.datasources.firebase.watchlist.FirestoreWatchlistDatasource;
import com.example.final_am_acn4a_debandi_juan.data.datasources.firebase.watchlist.WatchlistDatasource;
import com.example.final_am_acn4a_debandi_juan.data.datasources.network.RetrofitClient;
import com.example.final_am_acn4a_debandi_juan.data.datasources.network.TmdbDatasource;
import com.example.final_am_acn4a_debandi_juan.data.datasources.network.TmdbDatasourceImpl;
import com.example.final_am_acn4a_debandi_juan.data.datasources.network.api.TmdbApi;
import com.example.final_am_acn4a_debandi_juan.data.repositories.AuthRepository;
import com.example.final_am_acn4a_debandi_juan.data.repositories.GenreRepository;
import com.example.final_am_acn4a_debandi_juan.data.repositories.MovieRepository;
import com.example.final_am_acn4a_debandi_juan.data.repositories.WatchlistRepository;
import com.example.final_am_acn4a_debandi_juan.data.repositories.impl.AuthRepositoryImpl;
import com.example.final_am_acn4a_debandi_juan.data.repositories.impl.GenreRepositoryImpl;
import com.example.final_am_acn4a_debandi_juan.data.repositories.impl.MovieRepositoryImpl;
import com.example.final_am_acn4a_debandi_juan.data.repositories.impl.WatchlistRepositoryImpl;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AppModule {
    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final AuthRepository authRepository;
    private final WatchlistRepository watchlistRepository;

    public AppModule() {
        AuthDatasource authDatasource = new FirebaseAuthDatasource(FirebaseAuth.getInstance());
        authRepository = new AuthRepositoryImpl(authDatasource);

        WatchlistDatasource watchlistDatasource = new FirestoreWatchlistDatasource(FirebaseFirestore.getInstance());
        watchlistRepository = new WatchlistRepositoryImpl(watchlistDatasource, authRepository);

        TmdbApi api = RetrofitClient.getApi();
        TmdbDatasource networkDatasource = new TmdbDatasourceImpl(api);
        movieRepository = new MovieRepositoryImpl(networkDatasource);
        genreRepository = new GenreRepositoryImpl(networkDatasource);
    }

    public AuthRepository getAuthRepository() {
        return authRepository;
    }

    public WatchlistRepository getWatchlistRepository() {
        return watchlistRepository;
    }

    public MovieRepository getMovieRepository() {
        return movieRepository;
    }

    public GenreRepository getGenreRepository() {
        return genreRepository;
    }
}
