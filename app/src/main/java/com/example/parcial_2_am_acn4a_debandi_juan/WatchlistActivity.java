package com.example.parcial_2_am_acn4a_debandi_juan;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.parcial_2_am_acn4a_debandi_juan.data.WatchlistRepository;
import com.example.parcial_2_am_acn4a_debandi_juan.data.model.Movie;
import com.example.parcial_2_am_acn4a_debandi_juan.utils.AuthService;
import com.example.parcial_2_am_acn4a_debandi_juan.utils.MovieViewFactory;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class WatchlistActivity extends AppCompatActivity {

    private LinearLayout container;
    private TextView message;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_watchlist);

        container = findViewById(R.id.watchlist_Container);
        message = findViewById(R.id.watchlist_Message);
        progress = findViewById(R.id.watchlist_Progress);

        findViewById(R.id.watchlist_BtnBack).setOnClickListener(v -> finish());
        findViewById(R.id.watchlist_BtnSignout).setOnClickListener(v -> {
            AuthService.signout();
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.watchlist_root), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!AuthService.isLoggedIn()) {
            startActivity(new Intent(this, SigninActivity.class));
            finish();
            return;
        }
        loadWatchlist();
    }

    private void loadWatchlist() {
        container.removeAllViews();
        message.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
        WatchlistRepository.getAll(new WatchlistRepository.ListCallback() {
            @Override
            public void onResult(List<Movie> movies) {
                progress.setVisibility(View.GONE);
                renderResults(movies);
            }

            @Override
            public void onError(String errorMessage) {
                progress.setVisibility(View.GONE);
                showMessage(getString(R.string.error_network));
            }
        });
    }

    private void renderResults(List<Movie> movies) {
        container.removeAllViews();
        if (movies == null || movies.isEmpty()) {
            showMessage(getString(R.string.watchlist_empty));
            return;
        }
        message.setVisibility(View.GONE);
        for (Movie movie : movies) {
            final View[] cardHolder = new View[1];
            View card = MovieViewFactory.createWatchlistCard(this, movie, this::openDetail, true,
                    (m, nowInWatchlist) -> onWatchlistToggle(m, nowInWatchlist, cardHolder[0]));
            cardHolder[0] = card;
            container.addView(card);
        }
    }

    private void onWatchlistToggle(Movie movie, boolean nowInWatchlist, View card) {
        if (nowInWatchlist) {
            WatchlistRepository.add(movie, success -> showResult(success, R.string.watchlist_added));
        } else {
            container.removeView(card);
            if (container.getChildCount() == 0) {
                showMessage(getString(R.string.watchlist_empty));
            }
            WatchlistRepository.remove(movie.getId(), success -> showResult(success, R.string.watchlist_removed));
        }
    }

    private void showResult(boolean success, int successMessageRes) {
        int messageRes = success ? successMessageRes : R.string.error_network;
        Snackbar.make(findViewById(android.R.id.content), messageRes, Snackbar.LENGTH_SHORT).show();
    }

    private void openDetail(Movie movie) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(MovieDetailActivity.EXTRA_MOVIE_ID, movie.getId());
        intent.putExtra(MovieDetailActivity.EXTRA_MOVIE_TITLE, movie.getTitle());
        startActivity(intent);
    }

    private void showMessage(String text) {
        message.setText(text);
        message.setVisibility(View.VISIBLE);
    }
}