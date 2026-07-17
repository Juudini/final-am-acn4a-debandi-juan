package com.example.final_am_acn4a_debandi_juan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.final_am_acn4a_debandi_juan.data.models.Movie;
import com.example.final_am_acn4a_debandi_juan.data.models.MovieResponse;
import com.example.final_am_acn4a_debandi_juan.data.datasources.network.RetrofitClient;
import com.example.final_am_acn4a_debandi_juan.utils.BottomNavbarHelper;
import com.example.final_am_acn4a_debandi_juan.utils.MovieViewFactory;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GenreMoviesActivity extends AppCompatActivity {
    public static final String EXTRA_GENRE_ID = "extra_genre_id";
    public static final String EXTRA_GENRE_NAME = "extra_genre_name";
    private static final String SORT_BY = "popularity.desc";

    private LinearLayout resultsContainer;
    private TextView message;
    private ProgressBar progress;

    private int genreId;
    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;

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
        if (genreName != null) {
            title.setText(genreName);
        }

        genreId = getIntent().getIntExtra(EXTRA_GENRE_ID, -1);
        if (genreId <= 0) {
            finish();
            return;
        }

        scrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            View child = scrollView.getChildAt(0);
            if (child != null) {
                int diff = (child.getBottom() - (scrollView.getHeight() + scrollY));
                if (diff <= 0) {
                    loadMovies(genreId);
                }
            }
        });

        loadMovies(genreId);
    }

    private void loadMovies(int genreId) {
        if (isLoading || isLastPage) {
            return;
        }
        isLoading = true;
        if (currentPage == 1) {
            progress.setVisibility(View.VISIBLE);
            message.setVisibility(View.GONE);
        }
        RetrofitClient.getApi().discoverByGenre(genreId, SORT_BY, currentPage).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                isLoading = false;
                progress.setVisibility(View.GONE);
                if (!response.isSuccessful() || response.body() == null) {
                    if (currentPage == 1) {
                        showMessage(getString(R.string.error_network));
                    }
                    return;
                }
                renderResults(response.body().getResults());
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                isLoading = false;
                progress.setVisibility(View.GONE);
                if (currentPage == 1) {
                    showMessage(getString(R.string.error_network));
                }
            }
        });
    }

    private void renderResults(List<Movie> movies) {
        if (currentPage == 1) {
            resultsContainer.removeAllViews();
            if (movies == null || movies.isEmpty()) {
                showMessage(getString(R.string.categories_empty));
                isLastPage = true;
                return;
            }
        }
        if (movies == null || movies.isEmpty()) {
            isLastPage = true;
            return;
        }
        message.setVisibility(View.GONE);
        for (Movie movie : movies) {
            View card = MovieViewFactory.createListCard(this, movie, this::openDetail);
            resultsContainer.addView(card);
        }
        currentPage++;
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