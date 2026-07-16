package com.example.final_am_acn4a_debandi_juan;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.content.Intent;
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

import com.example.final_am_acn4a_debandi_juan.data.WatchlistRepository;
import com.example.final_am_acn4a_debandi_juan.data.model.CastMember;
import com.example.final_am_acn4a_debandi_juan.data.model.CreditsResponse;
import com.example.final_am_acn4a_debandi_juan.data.model.Genre;
import com.example.final_am_acn4a_debandi_juan.data.model.Movie;
import com.example.final_am_acn4a_debandi_juan.data.model.MovieDetail;
import com.example.final_am_acn4a_debandi_juan.data.network.RetrofitClient;
import com.example.final_am_acn4a_debandi_juan.utils.ImageLoader;
import com.example.final_am_acn4a_debandi_juan.utils.AuthService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailActivity extends AppCompatActivity {
    public static final String EXTRA_MOVIE_ID = "extra_movie_id";
    public static final String EXTRA_MOVIE_TITLE = "extra_movie_title";

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
    private ImageView star1, star2, star3, star4, star5;
    private int movieId;
    private MovieDetail currentDetail;
    private boolean inWatchlist;

    private List<CastMember> fullCast = new ArrayList<>();
    private int currentCastIndex = 0;
    private static final int CAST_PAGE_SIZE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_movie_detail);

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

        startSkeletonAnim(castSkeletonScroll);

        findViewById(R.id.detail_BtnBack).setOnClickListener(v -> finish());
        bookmarkButton.setOnClickListener(v -> onBookmarkClick());

        castScroll.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            View child = castScroll.getChildAt(0);
            if (child != null) {
                int diff = (child.getRight() - (castScroll.getWidth() + scrollX));
                if (diff <= 100) {
                    loadMoreCast();
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detail_root), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, 0, bars.right, bars.bottom);

            View btnBack = findViewById(R.id.detail_BtnBack);
            if (btnBack != null) {
                android.view.ViewGroup.MarginLayoutParams lp = (android.view.ViewGroup.MarginLayoutParams) btnBack.getLayoutParams();
                int baseMargin = getResources().getDimensionPixelSize(R.dimen.spacing_3);
                lp.topMargin = baseMargin + bars.top;
                btnBack.setLayoutParams(lp);
            }

            View scrim = findViewById(R.id.detail_statusBarScrim);
            if (scrim != null) {
                android.view.ViewGroup.LayoutParams lp = scrim.getLayoutParams();
                lp.height = bars.top + getResources().getDimensionPixelSize(R.dimen.spacing_16);
                scrim.setLayoutParams(lp);
            }

            return insets;
        });

        String initialTitle = getIntent().getStringExtra(EXTRA_MOVIE_TITLE);
        if (initialTitle != null) {
            title.setText(initialTitle);
        }

        movieId = getIntent().getIntExtra(EXTRA_MOVIE_ID, -1);
        if (movieId <= 0) {
            Toast.makeText(this, R.string.error_network, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        loadDetail(movieId);
        loadCredits(movieId);
        refreshWatchlistState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshWatchlistState();
    }

    private void refreshWatchlistState() {
        if (!AuthService.isLoggedIn()) {
            inWatchlist = false;
            updateBookmarkUi();
            return;
        }
        WatchlistRepository.isInWatchlist(movieId, result -> {
            inWatchlist = result;
            updateBookmarkUi();
        });
    }

    private void updateBookmarkUi() {
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

    private void onBookmarkClick() {
        if (!AuthService.isLoggedIn()) {
            startActivity(new Intent(this, SigninActivity.class));
            return;
        }
        if (currentDetail == null) {
            return;
        }
        if (inWatchlist) {
            WatchlistRepository.remove(movieId, success -> {
                if (success) {
                    inWatchlist = false;
                    updateBookmarkUi();
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), R.string.watchlist_removed, Snackbar.LENGTH_SHORT);
                    View anchor = findViewById(R.id.detail_bottomBar);
                    if (anchor != null) {
                        snackbar.setAnchorView(anchor);
                    }
                    snackbar.show();
                } else {
                    Toast.makeText(this, R.string.error_network, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Movie movie = new Movie(
                    currentDetail.getId(),
                    currentDetail.getTitle(),
                    currentDetail.getOverview(),
                    currentDetail.getPosterPath(),
                    currentDetail.getBackdropPath(),
                    currentDetail.getVoteAverage(),
                    currentDetail.getReleaseDate());
            List<Integer> genreIds = new ArrayList<>();
            if (currentDetail.getGenres() != null) {
                for (Genre g : currentDetail.getGenres()) {
                    genreIds.add(g.getId());
                }
            }
            movie.setGenreIds(genreIds);
            WatchlistRepository.add(movie, success -> {
                if (success) {
                    inWatchlist = true;
                    updateBookmarkUi();
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), R.string.watchlist_added, Snackbar.LENGTH_SHORT);
                    View anchor = findViewById(R.id.detail_bottomBar);
                    if (anchor != null) {
                        snackbar.setAnchorView(anchor);
                    }
                    snackbar.show();
                } else {
                    Toast.makeText(this, R.string.error_network, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadDetail(int movieId) {
        progress.setVisibility(View.VISIBLE);
        RetrofitClient.getApi().getMovieDetail(movieId).enqueue(new Callback<MovieDetail>() {
            @Override
            public void onResponse(@NonNull Call<MovieDetail> call, @NonNull Response<MovieDetail> response) {
                progress.setVisibility(View.GONE);
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(MovieDetailActivity.this, R.string.error_network, Toast.LENGTH_SHORT).show();
                    return;
                }
                bind(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<MovieDetail> call, @NonNull Throwable t) {
                progress.setVisibility(View.GONE);
                Toast.makeText(MovieDetailActivity.this, R.string.error_network, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCredits(int movieId) {
        RetrofitClient.getApi().getMovieCredits(movieId).enqueue(new Callback<CreditsResponse>() {
            @Override
            public void onResponse(@NonNull Call<CreditsResponse> call, @NonNull Response<CreditsResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    hideCastSkeleton();
                    return;
                }
                List<CastMember> cast = response.body().getCast();
                if (cast == null || cast.isEmpty()) {
                    hideCastSkeleton();
                    return;
                }
                fullCast = cast;
                currentCastIndex = 0;
                castContainer.removeAllViews();
                loadMoreCast();
            }

            @Override
            public void onFailure(@NonNull Call<CreditsResponse> call, @NonNull Throwable t) {
                hideCastSkeleton();
            }
        });
    }

    private void loadMoreCast() {
        if (fullCast == null || currentCastIndex >= fullCast.size()) {
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
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            itemParams.setMarginEnd(marginEnd);
            item.setLayoutParams(itemParams);

            int posterHeight = dp(104);
            MaterialCardView photoCard = new MaterialCardView(this);
            LinearLayout.LayoutParams photoParams = new LinearLayout.LayoutParams(cardSize, posterHeight);
            photoCard.setLayoutParams(photoParams);
            photoCard.setRadius(dp(8));
            photoCard.setStrokeWidth(0);
            photoCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.bg_navbar));
            photoCard.setCardElevation(0);

            ImageView photo = new ImageView(this);
            photo.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
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

            TextView tvName = new TextView(this);
            LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                    dp(80), ViewGroup.LayoutParams.WRAP_CONTENT);
            nameParams.topMargin = textPaddingTop;
            tvName.setLayoutParams(nameParams);
            tvName.setText(member.getName());
            tvName.setTextColor(ContextCompat.getColor(this, R.color.text_title));
            tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            tvName.setTypeface(null, Typeface.BOLD);
            tvName.setGravity(Gravity.CENTER_HORIZONTAL);
            tvName.setMaxLines(2);
            tvName.setEllipsize(TextUtils.TruncateAt.END);
            item.addView(tvName);

            if (!member.getCharacter().isEmpty()) {
                TextView tvCharacter = new TextView(this);
                LinearLayout.LayoutParams charParams = new LinearLayout.LayoutParams(
                        dp(80), ViewGroup.LayoutParams.WRAP_CONTENT);
                charParams.topMargin = textPaddingBetween;
                tvCharacter.setLayoutParams(charParams);
                tvCharacter.setText(member.getCharacter());
                tvCharacter.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
                tvCharacter.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                tvCharacter.setGravity(Gravity.CENTER_HORIZONTAL);
                tvCharacter.setMaxLines(1);
                tvCharacter.setEllipsize(TextUtils.TruncateAt.END);
                item.addView(tvCharacter);
            }

            castContainer.addView(item);
        }

        castSkeletonScroll.setVisibility(View.GONE);
        castScroll.setVisibility(View.VISIBLE);
    }

    private void hideCastSkeleton() {
        castSkeletonScroll.setVisibility(View.GONE);
    }

    private void bind(MovieDetail detail) {
        currentDetail = detail;
        title.setText(detail.getTitle());
        
        double rating5 = detail.getVoteAverage() / 2.0;
        rating.setText(String.format(java.util.Locale.US, "%.1f / 5", rating5));
        
        setStarIcon(star1, rating5, 1);
        setStarIcon(star2, rating5, 2);
        setStarIcon(star3, rating5, 3);
        setStarIcon(star4, rating5, 4);
        setStarIcon(star5, rating5, 5);

        overview.setText(detail.getOverview());
        ImageLoader.load(backdrop, detail.getBackdropPath() != null ? detail.getBackdropPath() : detail.getPosterPath());
        meta.setText(buildMeta(detail));
    }

    private void setStarIcon(ImageView star, double rating, int position) {
        if (star == null) return;
        if (rating >= position) {
            star.setImageResource(R.drawable.ic_star_rate);
        } else if (rating >= position - 0.5) {
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
        return android.text.TextUtils.join(" • ", parts);
    }

    private void startSkeletonAnim(View container) {
        if (container == null) return;
        if (container instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) container;
            for (int i = 0; i < vg.getChildCount(); i++) {
                startSkeletonAnim(vg.getChildAt(i));
            }
        } else {
            android.graphics.drawable.Drawable bg = container.getBackground();
            if (bg instanceof AnimationDrawable) {
                ((AnimationDrawable) bg).start();
            }
        }
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}