package com.example.final_am_acn4a_debandi_juan.ui.home;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
import com.example.final_am_acn4a_debandi_juan.data.models.Movie;
import com.example.final_am_acn4a_debandi_juan.di.AppModule;
import com.example.final_am_acn4a_debandi_juan.di.AppViewModelFactory;
import com.example.final_am_acn4a_debandi_juan.ui.auth.signin.SigninActivity;
import com.example.final_am_acn4a_debandi_juan.ui.common.image.ImageLoader;
import com.example.final_am_acn4a_debandi_juan.ui.common.movie.MovieViewFactory;
import com.example.final_am_acn4a_debandi_juan.ui.common.navigation.BottomNavbarHelper;
import com.example.final_am_acn4a_debandi_juan.ui.moviedetail.MovieDetailActivity;
import com.example.final_am_acn4a_debandi_juan.ui.newreleases.NewReleasesActivity;
import com.example.final_am_acn4a_debandi_juan.ui.search.SearchActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LinearLayout trendingMoviesContainer;
    private LinearLayout newReleasesContainer;
    private ScrollView mainScrollView;
    private HomeViewModel viewModel;

    private ImageView heroBgImage;
    private TextView heroLabel;
    private TextView heroDescription;
    private TextView heroRating;
    private Movie heroMovie;

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

        AppModule module = App.getModule(this);
        AppViewModelFactory factory = new AppViewModelFactory(module);
        viewModel = new ViewModelProvider(this, factory).get(HomeViewModel.class);
        viewModel.getState().observe(this, this::renderState);
        viewModel.getEvent().observe(this, this::handleEvent);

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
        trendingMoviesContainer = findViewById(R.id.trendingMoviesContainer);
        newReleasesContainer = findViewById(R.id.newReleasesContainer);

        findViewById(R.id.heroCard).setOnClickListener(v -> {
            if (heroMovie != null) {
                openDetail(heroMovie);
            }
        });
        findViewById(R.id.topHeader_BtnSearch).setOnClickListener(
            v -> startActivity(new Intent(this, SearchActivity.class))
        );
        findViewById(R.id.home_btnViewAllNewReleases).setOnClickListener(
            v -> startActivity(new Intent(this, NewReleasesActivity.class))
        );
        findViewById(R.id.hero_BtnBookmark).setOnClickListener(
            v -> viewModel.toggleHeroWatchlist()
        );
        BottomNavbarHelper.setup(this, BottomNavbarHelper.TAB_HOME);

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

    @Override
    protected void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.refreshHeroWatchlist();
        }
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

    private void bindHero(Movie movie) {
        heroMovie = movie;
        heroLabel.setText(movie.getTitle());
        heroDescription.setText(movie.getOverview());
        heroRating.setText(movie.getFormattedRating() + " / 5");
        ImageLoader.load(heroBgImage, movie.getBackdropPath() != null ? movie.getBackdropPath() : movie.getPosterPath());

        heroSkeletonOverlay.setVisibility(android.view.View.GONE);
        heroSkeletonTitleLines.setVisibility(android.view.View.GONE);
        heroSkeletonDescLines.setVisibility(android.view.View.GONE);
        heroBgImage.setVisibility(android.view.View.VISIBLE);
        heroPremiereBadge.setVisibility(android.view.View.VISIBLE);
        heroLabel.setVisibility(android.view.View.VISIBLE);
        heroDescription.setVisibility(android.view.View.VISIBLE);

        heroSkeletonOverlay.setVisibility(View.GONE);
        heroSkeletonTitleLines.setVisibility(View.GONE);
        heroSkeletonDescLines.setVisibility(View.GONE);
        heroBgImage.setVisibility(View.VISIBLE);
        heroPremiereBadge.setVisibility(View.VISIBLE);
        heroLabel.setVisibility(View.VISIBLE);
        heroDescription.setVisibility(View.VISIBLE);
    }

    private void updateHeroBookmarkUi(boolean inWatchlist, boolean loading) {
        MaterialButton button = findViewById(R.id.hero_BtnBookmark);
        if (button == null) {
            return;
        }

        button.setEnabled(heroMovie != null && !loading);
        if (inWatchlist) {
            button.setText(R.string.detail_inWatchlist);
            button.setIconResource(R.drawable.ic_watchlist);
            button.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            button.setTextColor(ContextCompat.getColor(this, R.color.text_title));
            button.setIconTint(
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.text_title))
            );
            button.setStrokeColor(
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.border_default))
            );
            button.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.stroke_width));
        } else {
            btn.setText(R.string.detail_addToWatchlist);
            btn.setIconResource(R.drawable.ic_add_to_watchlist);
            btn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.bg_selected)));
            btn.setTextColor(ContextCompat.getColor(this, R.color.text_selected));
            btn.setIconTint(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.icon_selected)));
            btn.setStrokeWidth(0);
        }
    }

    private void bindTrending(List<Movie> movies) {
        trendingMoviesContainer.removeAllViews();
        for (Movie movie : movies) {
            View card = MovieViewFactory.createPosterCard(this, movie, this::openDetail);
            trendingMoviesContainer.addView(card);
        }

        trendingSkeletonScroll.setVisibility(View.GONE);
        trendingMoviesContainer.setVisibility(View.VISIBLE);
    }

    private void bindNewReleases(List<Movie> movies) {
        newReleasesContainer.removeAllViews();
        for (Movie movie : movies) {
            View card = MovieViewFactory.createListCard(this, movie, this::openDetail);
            newReleasesContainer.addView(card);
        }

        newReleasesSkeletonContainer.setVisibility(View.GONE);
        newReleasesContainer.setVisibility(View.VISIBLE);
    }

    private void openDetail(Movie movie) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(MovieDetailActivity.EXTRA_MOVIE_ID, movie.getId());
        intent.putExtra(MovieDetailActivity.EXTRA_MOVIE_TITLE, movie.getTitle());
        startActivity(intent);
    }

    private void renderState(HomeUiState state) {
        switch (state.getStatus()) {
            case LOADING:
                startSkeletonAnimations();
                break;
            case CONTENT:
                Movie hero = state.getHeroMovie();
                if (hero != null) {
                    bindHero(hero);
                }
                bindTrending(state.getTrending());
                bindNewReleases(state.getNewReleases());
                break;
            case ERROR:
                Toast.makeText(this, R.string.error_network, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        updateHeroBookmarkUi(state.isHeroInWatchlist(), state.isWatchlistLoading());
    }

    private void handleEvent(HomeViewModel.Event event) {
        if (event == null) {
            return;
        }

        switch (event) {
            case NAVIGATE_TO_SIGNIN:
                startActivity(new Intent(this, SigninActivity.class));
                break;
            case ADDED_TO_WATCHLIST:
                showWatchlistSnackbar(R.string.watchlist_added);
                break;
            case REMOVED_FROM_WATCHLIST:
                showWatchlistSnackbar(R.string.watchlist_removed);
                break;
            case WATCHLIST_ERROR:
                Toast.makeText(this, R.string.error_network, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        viewModel.consumeEvent();
    }

    private void showWatchlistSnackbar(int messageRes) {
        Snackbar snackbar = Snackbar.make(
            findViewById(android.R.id.content),
            messageRes,
            Snackbar.LENGTH_SHORT
        );
        View anchor = findViewById(R.id.bottomNavbarWrapper);
        if (anchor != null) {
            snackbar.setAnchorView(anchor);
        }
        snackbar.show();
    }
}
