package com.example.parcial_2_am_acn4a_debandi_juan;

import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.parcial_2_am_acn4a_debandi_juan.data.WatchlistRepository;
import com.example.parcial_2_am_acn4a_debandi_juan.data.model.Movie;
import com.example.parcial_2_am_acn4a_debandi_juan.data.model.MovieResponse;
import com.example.parcial_2_am_acn4a_debandi_juan.data.network.RetrofitClient;
import com.example.parcial_2_am_acn4a_debandi_juan.utils.AuthService;
import com.example.parcial_2_am_acn4a_debandi_juan.utils.BottomNavbarHelper;
import com.example.parcial_2_am_acn4a_debandi_juan.utils.ImageLoader;
import com.example.parcial_2_am_acn4a_debandi_juan.utils.MovieViewFactory;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private LinearLayout trendingMoviesContainer;
    private LinearLayout newReleasesContainer;
    private ScrollView mainScrollView;

    // Hero
    private ImageView heroBgImage;
    private TextView heroLabel;
    private TextView heroDescription;
    private TextView heroRating;
    private Movie heroMovie;

    // Skeletons
    private View heroSkeletonOverlay;
    private View heroSkeletonTitleLines;
    private View heroSkeletonDescLines;
    private TextView heroPremiereBadge;
    private View trendingSkeletonScroll;
    private View newReleasesSkeletonContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        mainScrollView = findViewById(R.id.mainScrollView);
        setupHeaderScrollTransition();

        heroBgImage = findViewById(R.id.hero_BgImage);
        heroLabel = findViewById(R.id.heroSection_Label);
        heroDescription = findViewById(R.id.heroSection_Description);
        heroRating = findViewById(R.id.heroSection_Rating);
        heroSkeletonOverlay = findViewById(R.id.heroSkeletonOverlay);
        heroSkeletonTitleLines = findViewById(R.id.heroSkeletonTitleLines);
        heroSkeletonDescLines = findViewById(R.id.heroSkeletonDescLines);
        heroPremiereBadge = findViewById(R.id.heroSection_PremiereBadge);
        trendingSkeletonScroll = findViewById(R.id.trendingSkeletonScroll);
        newReleasesSkeletonContainer = findViewById(R.id.newReleasesSkeletonContainer);

        findViewById(R.id.heroCard).setOnClickListener(v -> {
            if (heroMovie != null) {
                openDetail(heroMovie);
            }
        });
        findViewById(R.id.topHeader_BtnSearch).setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
        BottomNavbarHelper.setup(this, BottomNavbarHelper.TAB_HOME);

        findViewById(R.id.hero_BtnBookmark).setOnClickListener(v -> {
            if (!AuthService.isLoggedIn()) {
                startActivity(new Intent(this, SigninActivity.class));
                return;
            }
            if (heroMovie == null) {
                return;
            }
            WatchlistRepository.add(heroMovie, success -> Toast.makeText(this, success ? R.string.watchlist_added : R.string.error_network, Toast.LENGTH_SHORT).show());
        });
        trendingMoviesContainer = findViewById(R.id.trendingMoviesContainer);
        newReleasesContainer = findViewById(R.id.newReleasesContainer);

        loadTrending();
        loadNowPlaying();
        startSkeletonAnimations();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);

            View topHeaderView = findViewById(R.id.topHeader);
            if (topHeaderView != null) {
                int paddingBase = getResources().getDimensionPixelSize(R.dimen.spacing_3);
                topHeaderView.setPadding(paddingBase, paddingBase + systemBars.top, paddingBase, paddingBase);
            }

            return insets;
        });
    }

    private void setupHeaderScrollTransition() {
        LinearLayout topHeader = findViewById(R.id.topHeader);
        if (topHeader == null || mainScrollView == null) {
            return;
        }
        int scrollThreshold = getResources().getDimensionPixelSize(R.dimen.header_scroll_threshold);
        int defaultStartColor = Color.parseColor("#FF000000");
        int defaultCenterColor = Color.parseColor("#A6000000");
        int defaultEndColor = Color.TRANSPARENT;
        int bgShadowColor = ContextCompat.getColor(this, R.color.bg_shadow);
        int bgLowestColor = ContextCompat.getColor(this, R.color.bg_lowest);

        android.graphics.drawable.GradientDrawable headerBg = new android.graphics.drawable.GradientDrawable(
                android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM,
                new int[] { defaultStartColor, defaultCenterColor, defaultEndColor }
        );
        topHeader.setBackground(headerBg);

        mainScrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            int startColor;
            int centerColor;
            int endColor;
            if (scrollY <= 0) {
                startColor = defaultStartColor;
                centerColor = defaultCenterColor;
                endColor = defaultEndColor;
            } else if (scrollY < scrollThreshold) {
                float ratio = (float) scrollY / scrollThreshold;
                startColor = androidx.core.graphics.ColorUtils.blendARGB(defaultStartColor, bgShadowColor, ratio);
                centerColor = androidx.core.graphics.ColorUtils.blendARGB(defaultCenterColor, bgShadowColor, ratio);
                endColor = androidx.core.graphics.ColorUtils.blendARGB(defaultEndColor, bgShadowColor, ratio);
            } else {
                float ratio = (float) (scrollY - scrollThreshold) / scrollThreshold;
                if (ratio > 1) ratio = 1;
                startColor = androidx.core.graphics.ColorUtils.blendARGB(bgShadowColor, bgLowestColor, ratio);
                centerColor = androidx.core.graphics.ColorUtils.blendARGB(bgShadowColor, bgLowestColor, ratio);
                endColor = androidx.core.graphics.ColorUtils.blendARGB(bgShadowColor, bgLowestColor, ratio);
            }
            headerBg.setColors(new int[] { startColor, centerColor, endColor });
        });
    }

    private void startSkeletonAnimations() {
        startAnimOnView(heroSkeletonOverlay);
        startAnimOnView(heroSkeletonTitleLines);
        startAnimOnView(heroSkeletonDescLines);
        startAnimOnView(trendingSkeletonScroll);
        startAnimOnView(newReleasesSkeletonContainer);
    }

    private void startAnimOnView(View container) {
        if (container == null) return;
        if (container instanceof android.view.ViewGroup) {
            android.view.ViewGroup vg = (android.view.ViewGroup) container;
            for (int i = 0; i < vg.getChildCount(); i++) {
                startAnimOnView(vg.getChildAt(i));
            }
        } else {
            android.graphics.drawable.Drawable bg = container.getBackground();
            if (bg instanceof AnimationDrawable) {
                ((AnimationDrawable) bg).start();
            }
        }
    }

    private void loadTrending() {
        RetrofitClient.getApi().getTrending().enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    showError();
                    return;
                }
                List<Movie> movies = response.body().getResults();
                if (movies == null || movies.isEmpty()) {
                    return;
                }
                bindHero(movies.get(0));
                renderTrending(movies);
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                showError();
            }
        });
    }

    private void loadNowPlaying() {
        RetrofitClient.getApi().getNowPlaying(1).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    showError();
                    return;
                }
                List<Movie> movies = response.body().getResults();
                if (movies != null) {
                    renderNewReleases(movies);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                showError();
            }
        });
    }

    private void bindHero(Movie movie) {
        heroMovie = movie;
        heroLabel.setText(movie.getTitle());
        heroDescription.setText(movie.getOverview());
        heroRating.setText(movie.getFormattedRating());
        ImageLoader.load(heroBgImage, movie.getBackdropPath() != null ? movie.getBackdropPath() : movie.getPosterPath());

        heroSkeletonOverlay.setVisibility(android.view.View.GONE);
        heroSkeletonTitleLines.setVisibility(android.view.View.GONE);
        heroSkeletonDescLines.setVisibility(android.view.View.GONE);
        heroBgImage.setVisibility(android.view.View.VISIBLE);
        heroPremiereBadge.setVisibility(android.view.View.VISIBLE);
        heroLabel.setVisibility(android.view.View.VISIBLE);
        heroDescription.setVisibility(android.view.View.VISIBLE);
    }

    private void renderTrending(List<Movie> movies) {
        trendingMoviesContainer.removeAllViews();
        for (Movie movie : movies) {
            View card = MovieViewFactory.createPosterCard(this, movie, this::openDetail);
            trendingMoviesContainer.addView(card);
        }

        trendingSkeletonScroll.setVisibility(android.view.View.GONE);
        trendingMoviesContainer.setVisibility(android.view.View.VISIBLE);
    }

    private void renderNewReleases(List<Movie> movies) {
        newReleasesContainer.removeAllViews();
        for (Movie movie : movies) {
            View card = MovieViewFactory.createListCard(this, movie, this::openDetail);
            newReleasesContainer.addView(card);
        }

        newReleasesSkeletonContainer.setVisibility(android.view.View.GONE);
        newReleasesContainer.setVisibility(android.view.View.VISIBLE);
    }

    private void openDetail(Movie movie) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(MovieDetailActivity.EXTRA_MOVIE_ID, movie.getId());
        intent.putExtra(MovieDetailActivity.EXTRA_MOVIE_TITLE, movie.getTitle());
        startActivity(intent);
    }

    private void showError() {
        Toast.makeText(this, R.string.error_network, Toast.LENGTH_SHORT).show();
    }
}