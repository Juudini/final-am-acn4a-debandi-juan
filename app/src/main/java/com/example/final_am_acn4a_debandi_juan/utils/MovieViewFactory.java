package com.example.final_am_acn4a_debandi_juan.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;

import com.example.final_am_acn4a_debandi_juan.R;
import com.example.final_am_acn4a_debandi_juan.data.GenreRepository;
import com.example.final_am_acn4a_debandi_juan.data.model.Movie;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public final class MovieViewFactory {
    public interface OnMovieClickListener {
        void onMovieClick(Movie movie);
    }

    public interface OnWatchlistToggleListener {
        void onWatchlistToggle(Movie movie, boolean nowInWatchlist);
    }

    private MovieViewFactory() {
    }

    /**
     * Card vertical
     */
    public static View createPosterCard(Context context, Movie movie, OnMovieClickListener listener) {
        int cardWidth = dp(context, R.dimen.movie_card_width);
        int cardHeight = dp(context, R.dimen.movie_card_height);
        int marginEnd = dp(context, R.dimen.spacing_4);
        int marginBottom = dp(context, R.dimen.spacing_2);
        int cornerRadius = dp(context, R.dimen.radius_md);

        LinearLayout cardLayout = new LinearLayout(context);
        cardLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(cardWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, marginEnd, 0);
        cardLayout.setLayoutParams(params);

        MaterialCardView imageCard = new MaterialCardView(context);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, cardHeight);
        cardParams.bottomMargin = marginBottom;
        imageCard.setLayoutParams(cardParams);
        imageCard.setRadius(cornerRadius);
        imageCard.setStrokeWidth(0);
        imageCard.setCardBackgroundColor(Color.TRANSPARENT);

        ImageView poster = new ImageView(context);
        poster.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        poster.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ImageLoader.load(poster, movie.getPosterPath());
        imageCard.addView(poster);

        TextView tvTitle = new TextView(context);
        tvTitle.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tvTitle.setText(movie.getTitle());
        tvTitle.setTextColor(ContextCompat.getColor(context, R.color.text_title));
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, sp(context, R.dimen.text_base));
        tvTitle.setTypeface(null, Typeface.BOLD);
        tvTitle.setMaxLines(1);
        tvTitle.setEllipsize(TextUtils.TruncateAt.END);

        TextView tvSubtitle = new TextView(context);
        tvSubtitle.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tvSubtitle.setText(movie.getReleaseYear());
        tvSubtitle.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
        tvSubtitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, sp(context, R.dimen.text_base));
        tvSubtitle.setLetterSpacing(0.05f);

        cardLayout.addView(imageCard);
        cardLayout.addView(tvTitle);
        cardLayout.addView(tvSubtitle);

        if (listener != null) {
            cardLayout.setOnClickListener(v -> listener.onMovieClick(movie));
        }
        return cardLayout;
    }

    /**
     * Card horizontal
     */
    public static View createListCard(Context context, Movie movie, OnMovieClickListener listener) {
        return buildListCard(context, movie, listener, true, false, false, null);
    }

    /**
     * Card horizontal con botón de watchlist (sin descripción).
     */
    public static View createWatchlistCard(Context context, Movie movie, OnMovieClickListener listener,
                                           boolean inWatchlist, OnWatchlistToggleListener toggleListener) {
        return buildListCard(context, movie, listener, false, true, inWatchlist, toggleListener);
    }

    private static View buildListCard(Context context, Movie movie, OnMovieClickListener listener,
                                      boolean showDescription, boolean showBookmark,
                                      boolean inWatchlist, OnWatchlistToggleListener toggleListener) {
        int posterWidth = dp(context, R.dimen.newRelease_card_width);
        int posterHeight = dp(context, R.dimen.newRelease_card_height);
        int marginStart = dp(context, R.dimen.spacing_4);
        int marginBottom = dp(context, R.dimen.spacing_4);
        int cornerRadius = dp(context, R.dimen.radius_md);

        LinearLayout rowLayout = new LinearLayout(context);
        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rowParams.bottomMargin = marginBottom;
        rowLayout.setLayoutParams(rowParams);

        MaterialCardView imageCard = new MaterialCardView(context);
        imageCard.setLayoutParams(new LinearLayout.LayoutParams(posterWidth, posterHeight));
        imageCard.setRadius(cornerRadius);
        imageCard.setStrokeWidth(0);
        imageCard.setCardBackgroundColor(Color.TRANSPARENT);

        ImageView poster = new ImageView(context);
        poster.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        poster.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ImageLoader.load(poster, movie.getPosterPath());
        imageCard.addView(poster);

        LinearLayout textContainer = new LinearLayout(context);
        textContainer.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        textParams.setMarginStart(marginStart);
        textParams.gravity = Gravity.CENTER_VERTICAL;
        textContainer.setLayoutParams(textParams);

        TextView tvTitle = new TextView(context);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvTitle.setLayoutParams(titleParams);
        tvTitle.setText(movie.getTitle());
        tvTitle.setTextColor(ContextCompat.getColor(context, R.color.text_title));
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, sp(context, R.dimen.text_lg));
        tvTitle.setTypeface(null, Typeface.BOLD);
        tvTitle.setMaxLines(2);
        tvTitle.setEllipsize(TextUtils.TruncateAt.END);

        String year = movie.getReleaseYear();
        String rating = movie.getFormattedRating();
        List<String> metaParts = new ArrayList<>();
        if (movie.getVoteAverage() > 0) {
            metaParts.add("★ " + rating);
        }
        if (year != null && !year.isEmpty()) {
            metaParts.add(year);
        }
        String metaText = TextUtils.join("   •   ", metaParts);

        TextView tvMeta = new TextView(context);
        LinearLayout.LayoutParams metaParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        metaParams.topMargin = dp(context, R.dimen.spacing_1);
        tvMeta.setLayoutParams(metaParams);
        tvMeta.setText(metaText);
        tvMeta.setTextColor(ContextCompat.getColor(context, R.color.text_title));
        tvMeta.setTextSize(TypedValue.COMPLEX_UNIT_PX, sp(context, R.dimen.text_xs));
        tvMeta.setTypeface(null, Typeface.BOLD);


        String categoriesText = GenreRepository.getGenresLabel(movie.getGenreIds());
        TextView tvCategories = new TextView(context);
        LinearLayout.LayoutParams catParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        catParams.topMargin = dp(context, R.dimen.spacing_1);
        tvCategories.setLayoutParams(catParams);
        tvCategories.setText(categoriesText);
        tvCategories.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
        tvCategories.setTextSize(TypedValue.COMPLEX_UNIT_PX, sp(context, R.dimen.text_xs));
        tvCategories.setVisibility(categoriesText.isEmpty() ? View.GONE : View.VISIBLE);

        textContainer.addView(tvTitle);
        textContainer.addView(tvMeta);
        textContainer.addView(tvCategories);

        if (showDescription) {
            TextView tvDesc = new TextView(context);
            LinearLayout.LayoutParams descParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            descParams.topMargin = dp(context, R.dimen.spacing_2);
            tvDesc.setLayoutParams(descParams);
            tvDesc.setText(movie.getOverview());
            tvDesc.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
            tvDesc.setTextSize(TypedValue.COMPLEX_UNIT_PX, sp(context, R.dimen.text_sm));
            tvDesc.setMaxLines(3);
            tvDesc.setEllipsize(TextUtils.TruncateAt.END);
            textContainer.addView(tvDesc);
        }

        rowLayout.addView(imageCard);
        rowLayout.addView(textContainer);

        if (showBookmark) {
            int btnSize = dp(context, R.dimen.icon_xl);
            MaterialButton bookmark = new MaterialButton(context);
            LinearLayout.LayoutParams bookmarkParams = new LinearLayout.LayoutParams(btnSize, btnSize);
            bookmarkParams.gravity = Gravity.CENTER_VERTICAL;
            bookmarkParams.setMarginStart(dp(context, R.dimen.spacing_2));
            bookmark.setLayoutParams(bookmarkParams);
            bookmark.setInsetTop(0);
            bookmark.setInsetBottom(0);
            bookmark.setIconResource(R.drawable.ic_add_to_watchlist);
            bookmark.setIconPadding(0);
            bookmark.setIconGravity(MaterialButton.ICON_GRAVITY_TEXT_START);
            bookmark.setCornerRadius(dp(context, R.dimen.radius_md));

            final boolean[] state = {inWatchlist};
            styleBookmark(context, bookmark, state[0]);
            bookmark.setOnClickListener(v -> {
                state[0] = !state[0];
                styleBookmark(context, bookmark, state[0]);
                if (toggleListener != null) {
                    toggleListener.onWatchlistToggle(movie, state[0]);
                }
            });
            rowLayout.addView(bookmark);
        }

        if (listener != null) {
            rowLayout.setOnClickListener(v -> listener.onMovieClick(movie));
        }
        return rowLayout;
    }

    private static void styleBookmark(Context context, MaterialButton button, boolean inWatchlist) {
        int yellow = ContextCompat.getColor(context, R.color.bg_selected);
        if (inWatchlist) {
            button.setIconResource(R.drawable.ic_watchlist);
            button.setBackgroundTintList(ColorStateList.valueOf(yellow));
            button.setIconTint(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.icon_selected)));
            button.setStrokeWidth(0);
        } else {
            button.setIconResource(R.drawable.ic_add_to_watchlist);
            button.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            button.setIconTint(ColorStateList.valueOf(yellow));
            button.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.border_default)));
            button.setStrokeWidth(dp(context, R.dimen.stroke_width));
        }
    }

    private static int dp(Context context, int dimenRes) {
        return context.getResources().getDimensionPixelSize(dimenRes);
    }
    private static float sp(Context context, int dimenRes) {
        return context.getResources().getDimension(dimenRes);
    }
}