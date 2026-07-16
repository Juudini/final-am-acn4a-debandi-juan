package com.example.final_am_acn4a_debandi_juan;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.final_am_acn4a_debandi_juan.data.GenreRepository;
import com.example.final_am_acn4a_debandi_juan.data.WatchlistRepository;
import com.example.final_am_acn4a_debandi_juan.data.model.Movie;
import com.example.final_am_acn4a_debandi_juan.utils.AuthService;
import com.example.final_am_acn4a_debandi_juan.utils.BottomNavbarHelper;
import com.example.final_am_acn4a_debandi_juan.utils.MovieViewFactory;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WatchlistActivity extends AppCompatActivity {

    private LinearLayout container;
    private TextView message;
    private ProgressBar progress;

    private final Handler watchlistHandler = new Handler(Looper.getMainLooper());
    private final Map<Integer, Runnable> pendingRemovals = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_watchlist);

        GenreRepository.init(null);

        container = findViewById(R.id.watchlist_Container);
        message = findViewById(R.id.watchlist_Message);
        progress = findViewById(R.id.watchlist_Progress);

        findViewById(R.id.watchlist_BtnSearch).setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
        BottomNavbarHelper.setup(this, BottomNavbarHelper.TAB_WATCHLIST);

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

    @Override
    protected void onPause() {
        super.onPause();
        flushPendingRemovals();
    }

    private void flushPendingRemovals() {
        if (pendingRemovals.isEmpty()) {
            return;
        }
        List<Runnable> copy = new ArrayList<>(pendingRemovals.values());
        pendingRemovals.clear();
        for (Runnable runnable : copy) {
            watchlistHandler.removeCallbacks(runnable);
            runnable.run();
        }
    }

    private void onWatchlistToggle(Movie movie, boolean nowInWatchlist, View card) {
        if (nowInWatchlist) {
            Runnable pending = pendingRemovals.remove(movie.getId());
            if (pending != null) {
                watchlistHandler.removeCallbacks(pending);
            } else {
                WatchlistRepository.add(movie, success -> showResult(success, R.string.watchlist_added));
            }
        } else {
            final int index = container.indexOfChild(card);
            container.removeView(card);
            if (container.getChildCount() == 0) {
                showMessage(getString(R.string.watchlist_empty));
            }

            Runnable removeRunnable = new Runnable() {
                @Override
                public void run() {
                    pendingRemovals.remove(movie.getId());
                    WatchlistRepository.remove(movie.getId(), success -> {});
                }
            };

            pendingRemovals.put(movie.getId(), removeRunnable);
            watchlistHandler.postDelayed(removeRunnable, 3500);

            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), R.string.watchlist_removed, 3500);
            View anchor = findViewById(R.id.bottomNavbarWrapper);
            if (anchor != null) {
                snackbar.setAnchorView(anchor);
            }
            snackbar.setAction(R.string.watchlist_undo, v -> {
                Runnable pending = pendingRemovals.remove(movie.getId());
                if (pending != null) {
                    watchlistHandler.removeCallbacks(pending);
                }
                if (index != -1 && index <= container.getChildCount()) {
                    container.addView(card, index);
                } else {
                    container.addView(card);
                }
                message.setVisibility(View.GONE);

                if (card instanceof ViewGroup) {
                    ViewGroup vg = (ViewGroup) card;
                    View lastChild = vg.getChildAt(vg.getChildCount() - 1);
                    if (lastChild instanceof MaterialButton) {
                        lastChild.performClick();
                    }
                }
            });
            snackbar.show();
        }
    }

    private void showResult(boolean success, int successMessageRes) {
        int messageRes = success ? successMessageRes : R.string.error_network;
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), messageRes, Snackbar.LENGTH_SHORT);
        View anchor = findViewById(R.id.bottomNavbarWrapper);
        if (anchor != null) {
            snackbar.setAnchorView(anchor);
        }
        snackbar.show();
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