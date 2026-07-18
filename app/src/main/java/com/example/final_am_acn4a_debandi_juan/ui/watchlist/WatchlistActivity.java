package com.example.final_am_acn4a_debandi_juan.ui.watchlist;

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
import androidx.lifecycle.ViewModelProvider;

import com.example.final_am_acn4a_debandi_juan.App;
import com.example.final_am_acn4a_debandi_juan.MovieDetailActivity;
import com.example.final_am_acn4a_debandi_juan.R;
import com.example.final_am_acn4a_debandi_juan.data.GenreRepository;
import com.example.final_am_acn4a_debandi_juan.data.models.Movie;
import com.example.final_am_acn4a_debandi_juan.di.AppViewModelFactory;
import com.example.final_am_acn4a_debandi_juan.ui.auth.signin.SigninActivity;
import com.example.final_am_acn4a_debandi_juan.ui.common.state.UiStatus;
import com.example.final_am_acn4a_debandi_juan.ui.search.SearchActivity;
import com.example.final_am_acn4a_debandi_juan.utils.BottomNavbarHelper;
import com.example.final_am_acn4a_debandi_juan.utils.MovieViewFactory;
import com.google.android.material.snackbar.Snackbar;
import java.util.List;

public class WatchlistActivity extends AppCompatActivity {
    private LinearLayout container;
    private TextView message;
    private ProgressBar progress;
    private WatchlistViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_watchlist);

        GenreRepository.init(null);

        container = findViewById(R.id.watchlist_Container);
        message = findViewById(R.id.watchlist_Message);
        progress = findViewById(R.id.watchlist_Progress);

        AppViewModelFactory factory = new AppViewModelFactory(App.getModule(this));
        viewModel = new ViewModelProvider(this, factory).get(WatchlistViewModel.class);
        viewModel.getState().observe(this, this::renderState);

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
        viewModel.load();
    }

    @Override
    protected void onPause() {
        viewModel.flushPendingRemovals();
        super.onPause();
    }

    private void renderState(WatchlistUiState state) {
        if (state.isAuthenticationRequired()) {
            startActivity(new Intent(this, SigninActivity.class));
            finish();
            return;
        }

        progress.setVisibility(state.getStatus() == UiStatus.LOADING ? View.VISIBLE : View.GONE);

        switch (state.getStatus()) {
            case LOADING:
                container.removeAllViews();
                message.setVisibility(View.GONE);
                break;
            case CONTENT:
                message.setVisibility(View.GONE);
                renderResults(state.getMovies());
                break;
            case EMPTY:
                container.removeAllViews();
                showMessage(getString(R.string.watchlist_empty));
                break;
            case ERROR:
                container.removeAllViews();
                showMessage(getString(R.string.error_network));
                break;
            default:
                break;
        }

        if (state.getMessage() != null) {
            showSnackbar(state.getMessage(), Snackbar.LENGTH_LONG);
        }
    }

    private void renderResults(List<Movie> movies) {
        container.removeAllViews();
        for (Movie movie : movies) {
            View card = MovieViewFactory.createWatchlistCard(
                this,
                movie,
                this::openDetail,
                true,
                (selectedMovie, nowInWatchlist) -> {
                    if (!nowInWatchlist) {
                        requestRemove(selectedMovie);
                    }
                });
            container.addView(card);
        }
    }

    private void requestRemove(Movie movie) {
        viewModel.requestRemove(movie);
        Snackbar snackbar = Snackbar.make(
            findViewById(android.R.id.content),
            R.string.watchlist_removed,
            3500);
        View anchor = findViewById(R.id.bottomNavbarWrapper);
        if (anchor != null) {
            snackbar.setAnchorView(anchor);
        }
        snackbar.setAction(R.string.watchlist_undo, v -> viewModel.undoRemove(movie));
        snackbar.show();
    }

    private void showSnackbar(String text, int duration) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), text, duration);
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