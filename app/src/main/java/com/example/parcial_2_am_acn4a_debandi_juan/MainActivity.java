package com.example.parcial_2_am_acn4a_debandi_juan;

import android.graphics.Color;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.parcial_2_am_acn4a_debandi_juan.data.model.Movie;
import com.example.parcial_2_am_acn4a_debandi_juan.data.model.MovieResponse;
import com.example.parcial_2_am_acn4a_debandi_juan.data.network.RetrofitClient;
import com.example.parcial_2_am_acn4a_debandi_juan.utils.AuthService;
import com.example.parcial_2_am_acn4a_debandi_juan.utils.ImageLoader;
import com.example.parcial_2_am_acn4a_debandi_juan.utils.MovieViewFactory;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private LinearLayout trendingMoviesContainer;
    private LinearLayout newReleasesContainer;
    private ScrollView mainScrollView;

    // Hero
    private ImageView heroBgImage;
    private TextView heroLabel;
    private TextView heroDescription;
    private TextView heroRating;
    private Movie heroMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        mainScrollView = findViewById(R.id.mainScrollView);
        LinearLayout topHeader = findViewById(R.id.topHeader);
        int scrollThreshold = getResources().getDimensionPixelSize(R.dimen.header_scroll_threshold);
        mainScrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY > scrollThreshold) {
                topHeader.setBackgroundColor(ContextCompat.getColor(this, R.color.bg_shadow));
            } else {
                topHeader.setBackgroundColor(Color.TRANSPARENT);
            }
        });

        heroBgImage = findViewById(R.id.hero_BgImage);
        heroLabel = findViewById(R.id.heroSection_Label);
        heroDescription = findViewById(R.id.heroSection_Description);
        heroRating = findViewById(R.id.heroSection_Rating);

        Button btnPlay = findViewById(R.id.heroSection_BtnPlay);
        btnPlay.setOnClickListener(v -> {
            if (heroMovie != null) {
                openDetail(heroMovie);
            }
        });
        findViewById(R.id.topHeader_BtnSearch).setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
        findViewById(R.id.bottomNavbar_BtnCategories).setOnClickListener(v -> startActivity(new Intent(this, CategoriesActivity.class)));
        findViewById(R.id.bottomNavbar_BtnAccount).setOnClickListener(v -> {
            if (AuthService.isLoggedIn()) {
                startActivity(new Intent(this, AccountActivity.class));
            } else {
                startActivity(new Intent(this, SigninActivity.class));
            }
        });
        findViewById(R.id.bottomNavbar_BtnWatchlist).setOnClickListener(v -> {
            if (AuthService.isLoggedIn()) {
                Toast.makeText(this, AuthService.getEmail(), Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(this, SigninActivity.class));
            }
        });
        trendingMoviesContainer = findViewById(R.id.trendingMoviesContainer);
        newReleasesContainer = findViewById(R.id.newReleasesContainer);

        loadTrending();
        loadNowPlaying();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadTrending() {
        RetrofitClient.getApi().getTrending().enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    showError();
                    return;
                }
                List<Movie> movies = response.body().getResults();
                if (movies == null || movies.isEmpty()) {
                    return;
                }
                bindHero(movies.get(0));
                renderTrending(movies);
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                showError();
            }
        });
    }

    private void loadNowPlaying() {
        RetrofitClient.getApi().getNowPlaying(1).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    showError();
                    return;
                }
                List<Movie> movies = response.body().getResults();
                if (movies != null) {
                    renderNewReleases(movies);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                showError();
            }
        });
    }

    private void bindHero(Movie movie) {
        heroMovie = movie;
        heroLabel.setText(movie.getTitle());
        heroDescription.setText(movie.getOverview());
        heroRating.setText(movie.getFormattedRating());
        ImageLoader.load(heroBgImage, movie.getBackdropPath() != null ? movie.getBackdropPath() : movie.getPosterPath());
    }

    private void renderTrending(List<Movie> movies) {
        trendingMoviesContainer.removeAllViews();
        for (Movie movie : movies) {
            View card = MovieViewFactory.createPosterCard(this, movie, this::openDetail);
            trendingMoviesContainer.addView(card);
        }
    }

    private void renderNewReleases(List<Movie> movies) {
        newReleasesContainer.removeAllViews();
        for (Movie movie : movies) {
            View card = MovieViewFactory.createListCard(this, movie, this::openDetail);
            newReleasesContainer.addView(card);
        }
    }

    private void openDetail(Movie movie) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(MovieDetailActivity.EXTRA_MOVIE_ID, movie.getId());
        intent.putExtra(MovieDetailActivity.EXTRA_MOVIE_TITLE, movie.getTitle());
        startActivity(intent);
    }

    private void showError() {
        Toast.makeText(this, R.string.error_network, Toast.LENGTH_SHORT).show();
    }
}