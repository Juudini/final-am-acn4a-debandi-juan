package com.example.parcial_2_am_acn4a_debandi_juan;

import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.content.Intent;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import com.example.parcial_2_am_acn4a_debandi_juan.data.WatchlistRepository;
import com.example.parcial_2_am_acn4a_debandi_juan.data.model.CastMember;
import com.example.parcial_2_am_acn4a_debandi_juan.data.model.CreditsResponse;
import com.example.parcial_2_am_acn4a_debandi_juan.data.model.Movie;
import com.example.parcial_2_am_acn4a_debandi_juan.data.model.MovieDetail;
import com.example.parcial_2_am_acn4a_debandi_juan.data.network.RetrofitClient;
import com.example.parcial_2_am_acn4a_debandi_juan.utils.ImageLoader;
import com.example.parcial_2_am_acn4a_debandi_juan.utils.AuthService;
import com.example.parcial_2_am_acn4a_debandi_juan.utils.BottomNavbarHelper;
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
    private Button bookmarkButton;
    private LinearLayout castContainer;
    private View castSkeletonScroll;
    private View castScroll;
    private int movieId;
    private MovieDetail currentDetail;
    private boolean inWatchlist;

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

        startSkeletonAnim(castSkeletonScroll);

        findViewById(R.id.detail_BtnBack).setOnClickListener(v -> finish());
        bookmarkButton.setOnClickListener(v -> onBookmarkClick());
        BottomNavbarHelper.setup(this, BottomNavbarHelper.TAB_NONE);

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
                lp.height = bars.top + getResources().getDimensionPixelSize(R.dimen.spacing_6);
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
        bookmarkButton.setText(inWatchlist ? R.string.detail_inWatchlist : R.string.detail_addToWatchlist);
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
                    Toast.makeText(this, R.string.watchlist_removed, Toast.LENGTH_SHORT).show();
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
            WatchlistRepository.add(movie, success -> {
                if (success) {
                    inWatchlist = true;
                    updateBookmarkUi();
                    Snackbar.make(findViewById(android.R.id.content),
                            R.string.watchlist_added, Snackbar.LENGTH_SHORT).show();
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
                int limit = Math.min(cast.size(), 20);
                renderCast(cast.subList(0, limit));
            }

            @Override
            public void onFailure(@NonNull Call<CreditsResponse> call, @NonNull Throwable t) {
                hideCastSkeleton();
            }
        });
    }

    private void renderCast(List<CastMember> cast) {
        castContainer.removeAllViews();
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
        rating.setText(detail.getFormattedRating());
        overview.setText(detail.getOverview());
        ImageLoader.load(backdrop, detail.getBackdropPath() != null ? detail.getBackdropPath() : detail.getPosterPath());
        meta.setText(buildMeta(detail));
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