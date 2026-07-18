package com.example.final_am_acn4a_debandi_juan.ui.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.final_am_acn4a_debandi_juan.ui.common.movie.MovieViewFactory;

import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private EditText input;
    private LinearLayout resultsContainer;
    private TextView message;
    private ProgressBar progress;
    private SearchViewModel viewModel;

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

        AppModule module = App.getModule(this);
        AppViewModelFactory factory = new AppViewModelFactory(module);
        viewModel = new ViewModelProvider(this, factory).get(SearchViewModel.class);

        btnClear.setVisibility(input.length() > 0 ? View.VISIBLE : View.GONE);
        findViewById(R.id.search_BtnBack).setOnClickListener(v -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search_root), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                btnClear.setVisibility(input.length() > 0 ? View.VISIBLE : View.GONE);
                viewModel.onQueryChanged(charSequence.toString());
            }

        });

        input.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard();
                viewModel.searchNow(input.getText().toString());
                return true;
            }

            return false;
        });

        btnClear.setOnClickListener(v -> {input.setText("");});

        scrollView.setOnScrollChangeListener(
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    if (scrollY > oldScrollY) {
                      hideKeyboard();
                    }

                    View child = scrollView.getChildAt(0);

                    if (child == null) {
                        return;
                    }

                    int difference = child.getBottom() - (scrollView.getHeight() + scrollY);

                    if (difference <= 0) {
                        viewModel.loadNextPage();
                    }
                }
          );

        viewModel.getState().observe(this, this::renderState);
        input.requestFocus();
    }

    private void renderState(SearchUiState state) {
        boolean showingProgress = state.getStatus() == UiStatus.LOADING || state.isLoadingNextPage();
        progress.setVisibility(showingProgress ? View.VISIBLE : View.GONE);

        switch (state.getStatus()) {
            case IDLE:
                resultsContainer.removeAllViews();
                showMessage(getString(R.string.search_prompt));
                break;

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
                showMessage(getString(R.string.search_empty));
            break;

            case ERROR:
                resultsContainer.removeAllViews();
                showMessage(getString(R.string.error_network));
            break;
        }
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
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        input.clearFocus();
    }
}