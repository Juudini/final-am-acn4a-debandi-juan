package com.example.final_am_acn4a_debandi_juan.ui.moviedetail;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.final_am_acn4a_debandi_juan.App;
import com.example.final_am_acn4a_debandi_juan.R;
import com.example.final_am_acn4a_debandi_juan.data.models.CastMember;
import com.example.final_am_acn4a_debandi_juan.data.models.MovieDetail;
import com.example.final_am_acn4a_debandi_juan.di.AppModule;
import com.example.final_am_acn4a_debandi_juan.ui.auth.signin.SigninActivity;
import com.example.final_am_acn4a_debandi_juan.ui.common.state.UiStatus;
import com.example.final_am_acn4a_debandi_juan.ui.common.image.ImageLoader;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MovieDetailActivity extends AppCompatActivity {
    public static final String EXTRA_MOVIE_ID = "extra_movie_id";
    public static final String EXTRA_MOVIE_TITLE = "extra_movie_title";
    private static final int CAST_PAGE_SIZE = 10;

    private ImageView backdrop;
    private TextView title;
    private TextView rating;
    private TextView meta;
    private TextView overview;
    private ProgressBar progress;
    private MaterialButton bookmarkButton;
    private LinearLayout castContainer;
    private View castSkeletonScroll;
    private HorizontalScrollView castScroll;
    private ImageView star1;
    private ImageView star2;
    private ImageView star3;
    private ImageView star4;
    private ImageView star5;
    private MovieDetailViewModel viewModel;
    private MovieDetail renderedDetail;
    private List<CastMember> fullCast = new ArrayList<>();
    private int currentCastIndex;
    private boolean creditsRendered;
    private boolean detailErrorShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_movie_detail);

        bindViews();
        startSkeletonAnim(castSkeletonScroll);
        configureListeners();
        configureInsets();

        String initialTitle = getIntent().getStringExtra(EXTRA_MOVIE_TITLE);
        if (initialTitle != null) {
            title.setText(initialTitle);
        }

        int movieId = getIntent().getIntExtra(EXTRA_MOVIE_ID, -1);
        if (movieId <= 0) {
            Toast.makeText(this, R.string.error_network, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        AppModule module = App.getModule(this);
        ViewModelProvider.Factory factory = new ViewModelProvider.Factory() {
            @NonNull
            @Override
            @SuppressWarnings("unchecked")
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass == MovieDetailViewModel.class) {
                return (T) new MovieDetailViewModel(
                    module.getMovieRepository(),
                    module.getWatchlistRepository(),
                    module.getAuthRepository(),
                    movieId
                );
            }
            throw new IllegalArgumentException("Unknown ViewModel: " + modelClass.getName());
            }
        };

        viewModel = new ViewModelProvider(this, factory).get(MovieDetailViewModel.class);
        viewModel.getState().observe(this, this::renderState);
        viewModel.getEvent().observe(this, this::handleEvent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.refreshWatchlist();
        }
    }

    private void bindViews() {
        backdrop = findViewById(R.id.detail_backdrop);
        title = findViewById(R.id.detail_title);
        rating = findViewById(R.id.detail_rating);
        meta = findViewById(R.id.detail_meta);
        overview = findViewById(R.id.detail_overview);
        progress = findViewById(R.id.detail_progress);
        bookmarkButton = findViewById(R.id.detail_BtnBookmark);
        castContainer = findViewById(R.id.detail_castContainer);
        castSkeletonScroll = findViewById(R.id.detail_castSkeletonScroll);
        castScroll = findViewById(R.id.detail_castScroll);
        star1 = findViewById(R.id.detail_star1);
        star2 = findViewById(R.id.detail_star2);
        star3 = findViewById(R.id.detail_star3);
        star4 = findViewById(R.id.detail_star4);
        star5 = findViewById(R.id.detail_star5);
    }

    private void configureListeners() {
        findViewById(R.id.detail_BtnBack).setOnClickListener(v -> finish());
        bookmarkButton.setOnClickListener(v -> viewModel.toggleWatchlist());
        castScroll.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            View child = castScroll.getChildAt(0);
            if (child != null) {
                int difference = child.getRight() - (castScroll.getWidth() + scrollX);
                if (difference <= 100) {
                    loadMoreCast();
                }
            }
        });
    }

    private void configureInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detail_root), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, 0, bars.right, bars.bottom);

            View btnBack = findViewById(R.id.detail_BtnBack);
            if (btnBack != null) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) btnBack.getLayoutParams();
                int baseMargin = getResources()
                        .getDimensionPixelSize(R.dimen.spacing_3);
                params.topMargin = baseMargin + bars.top;
                btnBack.setLayoutParams(params);
            }

            View scrim = findViewById(R.id.detail_statusBarScrim);
            if (scrim != null) {
                ViewGroup.LayoutParams params = scrim.getLayoutParams();
                params.height = bars.top + getResources()
                        .getDimensionPixelSize(R.dimen.spacing_16);
                scrim.setLayoutParams(params);
            }
            return insets;
        });
    }

    private void renderState(MovieDetailUiState state) {
        progress.setVisibility(state.getStatus() == UiStatus.LOADING ? View.VISIBLE : View.GONE);
        bookmarkButton.setEnabled(state.getDetail() != null && !state.isWatchlistLoading());
        updateBookmarkUi(state.isInWatchlist());

        if (state.getDetail() != null && renderedDetail != state.getDetail()) {
            renderedDetail = state.getDetail();
            bindDetail(renderedDetail);
        }

        if (!state.isCreditsLoading() && !creditsRendered) {
            creditsRendered = true;
            bindCredits(state.getCast());
        }

        if (state.getStatus() == UiStatus.ERROR && !detailErrorShown) {
            detailErrorShown = true;
            Toast.makeText(this, R.string.error_network, Toast.LENGTH_SHORT).show();
        }
    }

    private void handleEvent(MovieDetailViewModel.Event event) {
        if (event == null) {
            return;
        }
        switch (event) {
            case NAVIGATE_TO_SIGNIN:
                startActivity(new Intent(this, SigninActivity.class));
                break;
            case ADDED_TO_WATCHLIST:
                showWatchlistMessage(R.string.watchlist_added);
                break;
            case REMOVED_FROM_WATCHLIST:
                showWatchlistMessage(R.string.watchlist_removed);
                break;
            case WATCHLIST_ERROR:
                Toast.makeText(this, R.string.error_network, Toast.LENGTH_SHORT).show();
                break;
        }
        viewModel.consumeEvent();
    }

    private void bindDetail(MovieDetail detail) {
        title.setText(detail.getTitle());
        double ratingOutOfFive = detail.getVoteAverage() / 2.0;
        rating.setText(String.format(Locale.US, "%.1f / 5", ratingOutOfFive));
        setStarIcon(star1, ratingOutOfFive, 1);
        setStarIcon(star2, ratingOutOfFive, 2);
        setStarIcon(star3, ratingOutOfFive, 3);
        setStarIcon(star4, ratingOutOfFive, 4);
        setStarIcon(star5, ratingOutOfFive, 5);
        overview.setText(detail.getOverview());
        ImageLoader.load(backdrop, detail.getBackdropPath() != null ? detail.getBackdropPath() : detail.getPosterPath());
        meta.setText(buildMeta(detail));
    }

    private void bindCredits(List<CastMember> cast) {
        fullCast = cast != null ? new ArrayList<>(cast) : new ArrayList<>();
        currentCastIndex = 0;
        castContainer.removeAllViews();
        if (fullCast.isEmpty()) {
            hideCastSkeleton();
            return;
        }
        loadMoreCast();
    }

    private void loadMoreCast() {
        if (currentCastIndex >= fullCast.size()) {
            return;
        }
        int nextIndex = Math.min(currentCastIndex + CAST_PAGE_SIZE, fullCast.size());
        renderCast(fullCast.subList(currentCastIndex, nextIndex));
        currentCastIndex = nextIndex;
    }

    private void renderCast(List<CastMember> cast) {
        int cardSize = dp(72);
        int marginEnd = dp(16);
        int textPaddingTop = dp(8);
        int textPaddingBetween = dp(3);

        for (CastMember member : cast) {
            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.VERTICAL);
            item.setGravity(Gravity.CENTER_HORIZONTAL);
            LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            );
            itemParams.setMarginEnd(marginEnd);
            item.setLayoutParams(itemParams);

            MaterialCardView photoCard = new MaterialCardView(this);
            LinearLayout.LayoutParams photoParams = new LinearLayout.LayoutParams(cardSize, dp(104));
            photoCard.setLayoutParams(photoParams);
            photoCard.setRadius(dp(8));
            photoCard.setStrokeWidth(0);
            photoCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.bg_navbar));
            photoCard.setCardElevation(0);

            ImageView photo = new ImageView(this);
            photo.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
            );
            photo.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if (member.getProfilePath() != null) {
                ImageLoader.load(photo, member.getProfilePath());
            } else {
                photo.setImageResource(R.drawable.ic_account);
                photo.setColorFilter(ContextCompat.getColor(this, R.color.icon_default));
                photo.setPadding(dp(16), dp(16), dp(16), dp(16));
            }
            photoCard.addView(photo);
            item.addView(photoCard);

            TextView name = new TextView(this);
            LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                dp(80), ViewGroup.LayoutParams.WRAP_CONTENT
            );
            nameParams.topMargin = textPaddingTop;
            name.setLayoutParams(nameParams);
            name.setText(member.getName());
            name.setTextColor(ContextCompat.getColor(this, R.color.text_title));
            name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            name.setTypeface(null, Typeface.BOLD);
            name.setGravity(Gravity.CENTER_HORIZONTAL);
            name.setMaxLines(2);
            name.setEllipsize(TextUtils.TruncateAt.END);
            item.addView(name);

            if (!member.getCharacter().isEmpty()) {
                TextView character = new TextView(this);
                LinearLayout.LayoutParams characterParams = new LinearLayout.LayoutParams(
                    dp(80), ViewGroup.LayoutParams.WRAP_CONTENT
                );
                characterParams.topMargin = textPaddingBetween;
                character.setLayoutParams(characterParams);
                character.setText(member.getCharacter());
                character.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
                character.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                character.setGravity(Gravity.CENTER_HORIZONTAL);
                character.setMaxLines(1);
                character.setEllipsize(TextUtils.TruncateAt.END);
                item.addView(character);
            }
            castContainer.addView(item);
        }

        castSkeletonScroll.setVisibility(View.GONE);
        castScroll.setVisibility(View.VISIBLE);
    }

    private void hideCastSkeleton() {
        castSkeletonScroll.setVisibility(View.GONE);
        castScroll.setVisibility(View.GONE);
    }

    private void updateBookmarkUi(boolean inWatchlist) {
        if (inWatchlist) {
            bookmarkButton.setText(R.string.detail_inWatchlist);
            bookmarkButton.setIconResource(R.drawable.ic_watchlist);
            bookmarkButton.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            bookmarkButton.setTextColor(ContextCompat.getColor(this, R.color.text_title));
            bookmarkButton.setIconTint(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.text_title)));
            bookmarkButton.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.border_default)));
            bookmarkButton.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.stroke_width));
        } else {
            bookmarkButton.setText(R.string.detail_addToWatchlist);
            bookmarkButton.setIconResource(R.drawable.ic_add_to_watchlist);
            bookmarkButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.bg_selected)));
            bookmarkButton.setTextColor(ContextCompat.getColor(this, R.color.text_selected));
            bookmarkButton.setIconTint(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.icon_selected)));
            bookmarkButton.setStrokeWidth(0);
        }
    }

    private void showWatchlistMessage(int messageRes) {
        Snackbar snackbar = Snackbar.make(
            findViewById(android.R.id.content),
            messageRes,
            Snackbar.LENGTH_SHORT
        );
        View anchor = findViewById(R.id.detail_bottomBar);
        if (anchor != null) {
            snackbar.setAnchorView(anchor);
        }
        snackbar.show();
    }

    private void setStarIcon(ImageView star, double value, int position) {
        if (star == null) {
            return;
        }
        if (value >= position) {
            star.setImageResource(R.drawable.ic_star_rate);
        } else if (value >= position - 0.5) {
            star.setImageResource(R.drawable.ic_star_rate_half);
        } else {
            star.setImageResource(R.drawable.ic_star_rate_outline);
        }
    }

    private String buildMeta(MovieDetail detail) {
        List<String> parts = new ArrayList<>();
        if (!detail.getGenresLabel().isEmpty()) {
            parts.add(detail.getGenresLabel());
        }
        if (!detail.getFormattedRuntime().isEmpty()) {
            parts.add(detail.getFormattedRuntime());
        }
        if (!detail.getReleaseYear().isEmpty()) {
            parts.add(detail.getReleaseYear());
        }
        return TextUtils.join(" • ", parts);
    }

    private void startSkeletonAnim(View container) {
        if (container == null) {
            return;
        }
        if (container instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) container;
            for (int index = 0; index < group.getChildCount(); index++) {
                startSkeletonAnim(group.getChildAt(index));
            }
        } else if (container.getBackground() instanceof AnimationDrawable) {
            ((AnimationDrawable) container.getBackground()).start();
        }
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}