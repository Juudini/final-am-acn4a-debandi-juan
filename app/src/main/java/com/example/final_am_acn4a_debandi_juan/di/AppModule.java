package com.example.final_am_acn4a_debandi_juan.di;


import com.example.final_am_acn4a_debandi_juan.data.datasources.network.RetrofitClient;
import com.example.final_am_acn4a_debandi_juan.data.datasources.network.TmdbDatasource;
import com.example.final_am_acn4a_debandi_juan.data.datasources.network.TmdbDatasourceImpl;
import com.example.final_am_acn4a_debandi_juan.data.datasources.network.api.TmdbApi;
import com.example.final_am_acn4a_debandi_juan.data.repositories.MovieRepository;
import com.example.final_am_acn4a_debandi_juan.data.repositories.GenreRepository;
import com.example.final_am_acn4a_debandi_juan.data.repositories.impl.GenreRepositoryImpl;
import com.example.final_am_acn4a_debandi_juan.data.repositories.impl.MovieRepositoryImpl;

public class AppModule {
    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;

    public AppModule() {
        TmdbApi api = RetrofitClient.getApi();
        TmdbDatasource networkDatasource = new TmdbDatasourceImpl(api);

        movieRepository = new MovieRepositoryImpl(networkDatasource);
        genreRepository = new GenreRepositoryImpl(networkDatasource);
    }

    public MovieRepository getMovieRepository() {
        return movieRepository;
    }

    public GenreRepository getGenreRepository() {
        return genreRepository;
    }
}
