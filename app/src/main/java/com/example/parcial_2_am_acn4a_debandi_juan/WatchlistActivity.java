package com.example.parcial_2_am_acn4a_debandi_juan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.parcial_2_am_acn4a_debandi_juan.data.WatchlistRepository;
import com.example.parcial_2_am_acn4a_debandi_juan.data.model.Movie;
import com.example.parcial_2_am_acn4a_debandi_juan.util.AuthManager;
import com.example.parcial_2_am_acn4a_debandi_juan.util.MovieViewFactory;

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
        findViewById(R.id.watchlist_BtnLogout).setOnClickListener(v -> {
            AuthManager.logout();
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
        if (!AuthManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
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
            View card = MovieViewFactory.createListCard(this, movie, this::openDetail);
            container.addView(card);
        }
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
