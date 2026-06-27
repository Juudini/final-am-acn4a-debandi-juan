package com.example.parcial_2_am_acn4a_debandi_juan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
import com.example.parcial_2_am_acn4a_debandi_juan.util.MovieViewFactory;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    private EditText input;
    private LinearLayout resultsContainer;
    private TextView message;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);

        input = findViewById(R.id.search_Input);
        resultsContainer = findViewById(R.id.search_ResultsContainer);
        message = findViewById(R.id.search_Message);
        progress = findViewById(R.id.search_Progress);

        findViewById(R.id.search_BtnBack).setOnClickListener(v -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search_root), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        input.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });

        input.requestFocus();
    }

    private void performSearch() {
        String query = input.getText().toString().trim();
        if (query.isEmpty()) {
            return;
        }
        hideKeyboard();
        resultsContainer.removeAllViews();
        message.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);

        RetrofitClient.getApi().searchMovies(query, 1).enqueue(new Callback<MovieResponse>() {
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
            showMessage(getString(R.string.search_empty));
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

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
