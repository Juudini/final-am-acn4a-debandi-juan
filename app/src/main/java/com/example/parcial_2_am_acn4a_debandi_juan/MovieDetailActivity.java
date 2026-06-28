package com.example.parcial_2_am_acn4a_debandi_juan;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.parcial_2_am_acn4a_debandi_juan.data.WatchlistRepository;
import com.example.parcial_2_am_acn4a_debandi_juan.data.model.Movie;
import com.example.parcial_2_am_acn4a_debandi_juan.data.model.MovieDetail;
import com.example.parcial_2_am_acn4a_debandi_juan.data.network.RetrofitClient;
import com.example.parcial_2_am_acn4a_debandi_juan.utils.ImageLoader;
import com.example.parcial_2_am_acn4a_debandi_juan.utils.AuthService;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailActivity extends AppCompatActivity {
    public static final String EXTRA_MOVIE_ID = "extra_movie_id";
    public static final String EXTRA_MOVIE_TITLE = "extra_movie_title";

    private ImageView backdrop;
    private TextView title;
    private TextView rating;
    private TextView meta;
    private TextView overview;
    private ProgressBar progress;
    private Button bookmarkButton;
    private int movieId;
    private MovieDetail currentDetail;
    private boolean inWatchlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_movie_detail);

        backdrop = findViewById(R.id.detail_backdrop);
        title = findViewById(R.id.detail_title);
        rating = findViewById(R.id.detail_rating);
        meta = findViewById(R.id.detail_meta);
        overview = findViewById(R.id.detail_overview);
        progress = findViewById(R.id.detail_progress);
        bookmarkButton = findViewById(R.id.detail_BtnBookmark);

        findViewById(R.id.detail_BtnBack).setOnClickListener(v -> finish());
        bookmarkButton.setOnClickListener(v -> onBookmarkClick());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detail_root), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, 0, 0, bars.bottom);
            return insets;
        });

        String initialTitle = getIntent().getStringExtra(EXTRA_MOVIE_TITLE);
        if (initialTitle != null) {
            title.setText(initialTitle);
        }

        movieId = getIntent().getIntExtra(EXTRA_MOVIE_ID, -1);
        if (movieId <= 0) {
            Toast.makeText(this, R.string.error_network, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        loadDetail(movieId);
        refreshWatchlistState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshWatchlistState();
    }

    private void refreshWatchlistState() {
        if (!AuthService.isLoggedIn()) {
            inWatchlist = false;
            updateBookmarkUi();
            return;
        }
        WatchlistRepository.isInWatchlist(movieId, result -> {
            inWatchlist = result;
            updateBookmarkUi();
        });
    }

    private void updateBookmarkUi() {
        bookmarkButton.setText(inWatchlist ? R.string.detail_inWatchlist : R.string.detail_addToWatchlist);
    }

    private void onBookmarkClick() {
        if (!AuthService.isLoggedIn()) {
            startActivity(new Intent(this, SigninActivity.class));
            return;
        }
        if (currentDetail == null) {
            return;
        }
        if (inWatchlist) {
            WatchlistRepository.remove(movieId, success -> {
                if (success) {
                    inWatchlist = false;
                    updateBookmarkUi();
                    Toast.makeText(this, R.string.watchlist_removed, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.error_network, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Movie movie = new Movie(
                    currentDetail.getId(),
                    currentDetail.getTitle(),
                    currentDetail.getOverview(),
                    currentDetail.getPosterPath(),
                    currentDetail.getBackdropPath(),
                    currentDetail.getVoteAverage(),
                    currentDetail.getReleaseDate());
            WatchlistRepository.add(movie, success -> {
                if (success) {
                    inWatchlist = true;
                    updateBookmarkUi();
                    Snackbar.make(findViewById(android.R.id.content),
                            R.string.watchlist_added, Snackbar.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.error_network, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadDetail(int movieId) {
        progress.setVisibility(View.VISIBLE);
        RetrofitClient.getApi().getMovieDetail(movieId).enqueue(new Callback<MovieDetail>() {
            @Override
            public void onResponse(@NonNull Call<MovieDetail> call, @NonNull Response<MovieDetail> response) {
                progress.setVisibility(View.GONE);
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(MovieDetailActivity.this, R.string.error_network, Toast.LENGTH_SHORT).show();
                    return;
                }
                bind(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<MovieDetail> call, @NonNull Throwable t) {
                progress.setVisibility(View.GONE);
                Toast.makeText(MovieDetailActivity.this, R.string.error_network, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bind(MovieDetail detail) {
        currentDetail = detail;
        title.setText(detail.getTitle());
        rating.setText(detail.getFormattedRating());
        overview.setText(detail.getOverview());
        ImageLoader.load(backdrop, detail.getBackdropPath() != null ? detail.getBackdropPath() : detail.getPosterPath());
        meta.setText(buildMeta(detail));
    }

    private String buildMeta(MovieDetail detail) {
        List<String> parts = new ArrayList<>();
        if (!detail.getGenresLabel().isEmpty()) {
            parts.add(detail.getGenresLabel());
        }
        if (!detail.getFormattedRuntime().isEmpty()) {
            parts.add(detail.getFormattedRuntime());
        }
        if (!detail.getReleaseYear().isEmpty()) {
            parts.add(detail.getReleaseYear());
        }
        return android.text.TextUtils.join(" • ", parts);
    }
}