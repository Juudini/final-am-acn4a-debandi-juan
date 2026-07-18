package com.example.final_am_acn4a_debandi_juan.ui.genremovies;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.final_am_acn4a_debandi_juan.App;
import com.example.final_am_acn4a_debandi_juan.ui.moviedetail.MovieDetailActivity;
import com.example.final_am_acn4a_debandi_juan.R;
import com.example.final_am_acn4a_debandi_juan.data.models.Movie;
import com.example.final_am_acn4a_debandi_juan.di.AppModule;
import com.example.final_am_acn4a_debandi_juan.ui.common.state.UiStatus;
import com.example.final_am_acn4a_debandi_juan.ui.common.navigation.BottomNavbarHelper;
import com.example.final_am_acn4a_debandi_juan.ui.common.movie.MovieViewFactory;
import java.util.List;

public class GenreMoviesActivity extends AppCompatActivity {
    public static final String EXTRA_GENRE_ID = "extra_genre_id";
    public static final String EXTRA_GENRE_NAME = "extra_genre_name";
    private LinearLayout resultsContainer;
    private TextView message;
    private ProgressBar progress;

    private GenreMoviesViewModel viewModel;

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
        BottomNavbarHelper.setup(this, BottomNavbarHelper.TAB_CATEGORIES);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.genre_root), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        String genreName = getIntent().getStringExtra(EXTRA_GENRE_NAME);
        TextView title = findViewById(R.id.genre_Title);
        if (genreName != null && !genreName.isEmpty()) {
            title.setText(genreName);
        }

        int genreId = getIntent().getIntExtra(EXTRA_GENRE_ID, -1);

        if (genreId <= 0) {
            finish();
            return;
        }

        AppModule container = App.getModule(this);
        ViewModelProvider.Factory factory = new ViewModelProvider.Factory() {
            @NonNull
            @Override
            @SuppressWarnings("unchecked")
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new GenreMoviesViewModel(container.getMovieRepository(), genreId);
            }
        };

        viewModel = new ViewModelProvider(this, factory).get(GenreMoviesViewModel.class);

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
    private void renderState(GenreMoviesUiState state) {
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