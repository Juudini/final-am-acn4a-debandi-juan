package com.example.final_am_acn4a_debandi_juan.ui.categories;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.final_am_acn4a_debandi_juan.App;
import com.example.final_am_acn4a_debandi_juan.R;
import com.example.final_am_acn4a_debandi_juan.data.models.Genre;
import com.example.final_am_acn4a_debandi_juan.di.AppModule;
import com.example.final_am_acn4a_debandi_juan.di.AppViewModelFactory;
import com.example.final_am_acn4a_debandi_juan.ui.common.state.UiStatus;
import com.example.final_am_acn4a_debandi_juan.ui.genremovies.GenreMoviesActivity;
import com.example.final_am_acn4a_debandi_juan.ui.search.SearchActivity;
import com.example.final_am_acn4a_debandi_juan.utils.BottomNavbarHelper;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class CategoriesActivity extends AppCompatActivity {
    private LinearLayout container;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_categories);

        container = findViewById(R.id.categories_Container);
        progress = findViewById(R.id.categories_Progress);

        findViewById(R.id.categories_BtnSearch).setOnClickListener(v-> startActivity(new Intent(this, SearchActivity.class)));
        BottomNavbarHelper.setup(this, BottomNavbarHelper.TAB_CATEGORIES);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.categories_root), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        AppModule module = App.getModule(this);
        AppViewModelFactory factory = new AppViewModelFactory(module);
        CategoriesViewModel viewModel = new ViewModelProvider(this, factory).get(CategoriesViewModel.class);
        viewModel.getState().observe(this, this::renderState);
    }

    private void renderState(CategoriesUiState state) {
        progress.setVisibility(state.getStatus() == UiStatus.LOADING ? View.VISIBLE : View.GONE);

        switch (state.getStatus()) {
            case LOADING:
                container.removeAllViews();
                break;

            case CONTENT:
                renderGenres(state.getGenres());
                break;

            case EMPTY:
                container.removeAllViews();

                Toast.makeText(this, R.string.categories_empty, Toast.LENGTH_SHORT).show();
                break;

            case ERROR:
                container.removeAllViews();
                Toast.makeText(this, R.string.error_network, Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }
    }

    private void renderGenres(List<Genre> genres) {
        container.removeAllViews();
        for (Genre genre : genres) {
            container.addView(createGenreRow(genre));
        }
    }

    private View createGenreRow(Genre genre) {
        int padding = getResources().getDimensionPixelSize(R.dimen.spacing_4);
        int marginBottom = getResources().getDimensionPixelSize(R.dimen.spacing_3);
        int cornerRadius = getResources().getDimensionPixelSize(R.dimen.radius_lg);

        MaterialCardView card = new MaterialCardView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = marginBottom;
        card.setLayoutParams(params);
        card.setRadius(cornerRadius);
        card.setStrokeWidth(0);
        card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.bg_navbar));

        TextView name = new TextView(this);
        name.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        name.setText(genre.getName());
        name.setTextColor(ContextCompat.getColor(this, R.color.text_title));
        name.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_lg));
        name.setTypeface(null, Typeface.BOLD);
        name.setGravity(Gravity.CENTER_VERTICAL);
        name.setPadding(padding, padding, padding, padding);

        card.addView(name);
        card.setOnClickListener(v -> openGenre(genre));
        return card;
    }

    private void openGenre(Genre genre) {
        Intent intent = new Intent(this, GenreMoviesActivity.class);
        intent.putExtra(GenreMoviesActivity.EXTRA_GENRE_ID, genre.getId());
        intent.putExtra(GenreMoviesActivity.EXTRA_GENRE_NAME, genre.getName());
        startActivity(intent);
    }
}