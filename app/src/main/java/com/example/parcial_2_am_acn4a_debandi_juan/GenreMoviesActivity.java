package com.example.parcial_2_am_acn4a_debandi_juan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.parcial_2_am_acn4a_debandi_juan.data.model.Movie;
import com.example.parcial_2_am_acn4a_debandi_juan.data.model.MovieResponse;
import com.example.parcial_2_am_acn4a_debandi_juan.data.network.RetrofitClient;
import com.example.parcial_2_am_acn4a_debandi_juan.utils.MovieViewFactory;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_genre_movies);

        resultsContainer = findViewById(R.id.genre_ResultsContainer);
        message = findViewById(R.id.genre_Message);
        progress = findViewById(R.id.genre_Progress);

        findViewById(R.id.genre_BtnBack).setOnClickListener(v -> finish());

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

        int genreId = getIntent().getIntExtra(EXTRA_GENRE_ID, -1);
        if (genreId <= 0) {
            finish();
            return;
        }
        loadMovies(genreId);
    }

    private void loadMovies(int genreId) {
        progress.setVisibility(View.VISIBLE);
        message.setVisibility(View.GONE);
        RetrofitClient.getApi().discoverByGenre(genreId, SORT_BY, 1).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                progress.setVisibility(View.GONE);
                if (!response.isSuccessful() || response.body() == null) {
                    showMessage(getString(R.string.error_network));
                    return;
                }
                renderResults(response.body().getResults());
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                progress.setVisibility(View.GONE);
                showMessage(getString(R.string.error_network));
            }
        });
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