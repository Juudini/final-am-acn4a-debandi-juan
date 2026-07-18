package com.example.final_am_acn4a_debandi_juan.ui.newreleases;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.final_am_acn4a_debandi_juan.App;
import com.example.final_am_acn4a_debandi_juan.ui.moviedetail.MovieDetailActivity;
import com.example.final_am_acn4a_debandi_juan.R;
import com.example.final_am_acn4a_debandi_juan.data.models.Movie;
import com.example.final_am_acn4a_debandi_juan.di.AppModule;
import com.example.final_am_acn4a_debandi_juan.di.AppViewModelFactory;
import com.example.final_am_acn4a_debandi_juan.ui.common.state.UiStatus;
import com.example.final_am_acn4a_debandi_juan.utils.MovieViewFactory;

import java.util.List;

public class NewReleasesActivity extends AppCompatActivity {
    private LinearLayout resultsContainer;
    private TextView message;
    private ProgressBar progress;
    private NewReleasesViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_genre_movies);

        resultsContainer = findViewById(R.id.genre_ResultsContainer);
        message = findViewById(R.id.genre_Message);
        progress = findViewById(R.id.genre_Progress);
        ScrollView scrollView = findViewById(R.id.genre_Scroll);

        findViewById(R.id.genre_BtnBack).setOnClickListener(v -> finish());

        View bottomNavbar = findViewById(R.id.bottomNavbarWrapper);
        if (bottomNavbar != null) {
            bottomNavbar.setVisibility(View.GONE);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.genre_root), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        TextView title = findViewById(R.id.genre_Title);
        title.setText(R.string.section_newReleases);

        AppModule module = App.getModule(this);
        AppViewModelFactory factory = new AppViewModelFactory(module);
        viewModel = new ViewModelProvider(this, factory).get(NewReleasesViewModel.class);
        viewModel.getState().observe(this, this::renderState);

        scrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            View child = scrollView.getChildAt(0);
            if (child != null) {
                int diff = (child.getBottom() - (scrollView.getHeight() + scrollY));
                if (diff <= 0) {
                    viewModel.loadNextPage();
                }
            }
        });
    }

    private void renderState(NewReleasesUiState state) {
        boolean showingProgress = state.getStatus() == UiStatus.LOADING || state.isLoadingNextPage();

        progress.setVisibility(showingProgress ? View.VISIBLE : View.GONE);

        switch (state.getStatus()) {
            case LOADING:
                resultsContainer.removeAllViews();
                message.setVisibility(View.GONE);
                break;

            case CONTENT:
                renderResults(state.getMovies());
                if (state.getMessage() != null && !state.getMessage().isEmpty()) {
                    Toast.makeText(this, R.string.error_network, Toast.LENGTH_SHORT).show();
                }
                break;

            case EMPTY:
                resultsContainer.removeAllViews();
                showMessage(getString(R.string.categories_empty));
                break;

            case ERROR:
                resultsContainer.removeAllViews();
                showMessage(getString(R.string.error_network));
                break;

            default:
                break;
        }
    }

    private void renderResults(List<Movie> movies) {
        resultsContainer.removeAllViews();

        if (movies == null || movies.isEmpty()) {
            showMessage(getString(R.string.categories_empty));
            return;
        }

        message.setVisibility(View.GONE);
        for (Movie movie : movies) {
            View card = MovieViewFactory.createListCard(this, movie, this::openDetail);
            resultsContainer.addView(card);
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
