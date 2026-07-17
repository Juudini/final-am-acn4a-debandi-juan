package com.example.final_am_acn4a_debandi_juan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
import com.example.final_am_acn4a_debandi_juan.utils.MovieViewFactory;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    private EditText input;
    private LinearLayout resultsContainer;
    private TextView message;
    private ProgressBar progress;

    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private String currentQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);

        input = findViewById(R.id.search_Input);
        resultsContainer = findViewById(R.id.search_ResultsContainer);
        message = findViewById(R.id.search_Message);
        progress = findViewById(R.id.search_Progress);
        View btnClear = findViewById(R.id.search_BtnClear);
        ScrollView scrollView = findViewById(R.id.search_Scroll);

        findViewById(R.id.search_BtnBack).setOnClickListener(v -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search_root), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnClear.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
                if (s.toString().trim().isEmpty()) {
                    resultsContainer.removeAllViews();
                    progress.setVisibility(View.GONE);
                    showMessage(getString(R.string.search_prompt));
                    currentPage = 1;
                    isLastPage = false;
                    isLoading = false;
                    currentQuery = "";
                } else {
                    searchRunnable = () -> performSearch(false);
                    searchHandler.postDelayed(searchRunnable, 600);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        input.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
                performSearch(true);
                return true;
            }
            return false;
        });

        btnClear.setOnClickListener(v -> {
            input.setText("");
            if (searchRunnable != null) {
                searchHandler.removeCallbacks(searchRunnable);
            }
            resultsContainer.removeAllViews();
            progress.setVisibility(View.GONE);
            showMessage(getString(R.string.search_prompt));
            currentPage = 1;
            isLastPage = false;
            isLoading = false;
            currentQuery = "";
        });

        scrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            View child = scrollView.getChildAt(0);
            if (child != null) {
                int diff = (child.getBottom() - (scrollView.getHeight() + scrollY));
                if (diff <= 0) {
                    performSearch(false);
                }
            }
        });

        input.requestFocus();
    }

    private void performSearch(boolean hideKeyboard) {
        String query = input.getText().toString().trim();
        if (query.isEmpty()) {
            resultsContainer.removeAllViews();
            progress.setVisibility(View.GONE);
            showMessage(getString(R.string.search_prompt));
            currentPage = 1;
            isLastPage = false;
            isLoading = false;
            currentQuery = "";
            return;
        }

        if (!query.equals(currentQuery)) {
            currentPage = 1;
            isLastPage = false;
            isLoading = false;
            currentQuery = query;
        }

        if (isLoading || isLastPage) {
            return;
        }

        isLoading = true;

        if (hideKeyboard) {
            hideKeyboard();
        }

        if (currentPage == 1) {
            resultsContainer.removeAllViews();
            message.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
        }

        RetrofitClient.getApi().searchMovies(query, currentPage).enqueue(new Callback<MovieResponse>() {
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
                showMessage(getString(R.string.search_empty));
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

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}