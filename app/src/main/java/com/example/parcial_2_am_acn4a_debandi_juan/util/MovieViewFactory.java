package com.example.parcial_2_am_acn4a_debandi_juan.util;

import android.content.Context;
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

import com.example.parcial_2_am_acn4a_debandi_juan.R;
import com.example.parcial_2_am_acn4a_debandi_juan.data.model.Movie;
import com.google.android.material.card.MaterialCardView;

/**
 * Construye programáticamente las cards de película reutilizadas por el Home,
 * la Búsqueda, las Categorías y la Watchlist. Centraliza el estilo para evitar
 * duplicación entre Activities.
 */
public final class MovieViewFactory {

    public interface OnMovieClickListener {
        void onMovieClick(Movie movie);
    }

    private MovieViewFactory() {
    }

    /**
     * Cards verticales (poster + title + año).
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
        tvSubtitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, sp(context, R.dimen.text_xs));
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
     * Card horizontal en lista (poster + title/rating + año + sinopsis)
     */
    public static View createListCard(Context context, Movie movie, OnMovieClickListener listener) {
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

        LinearLayout titleRow = new LinearLayout(context);
        titleRow.setOrientation(LinearLayout.HORIZONTAL);
        titleRow.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        titleRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView tvTitle = new TextView(context);
        tvTitle.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
        tvTitle.setText(movie.getTitle());
        tvTitle.setTextColor(ContextCompat.getColor(context, R.color.text_title));
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, sp(context, R.dimen.text_lg));
        tvTitle.setTypeface(null, Typeface.BOLD);
        tvTitle.setMaxLines(2);
        tvTitle.setEllipsize(TextUtils.TruncateAt.END);

        ImageView starIcon = new ImageView(context);
        int starSize = dp(context, R.dimen.spacing_4);
        starIcon.setLayoutParams(new LinearLayout.LayoutParams(starSize, starSize));
        starIcon.setImageResource(R.drawable.ic_star_rate);
        starIcon.setColorFilter(ContextCompat.getColor(context, R.color.bg_selected));

        TextView tvRating = new TextView(context);
        LinearLayout.LayoutParams ratingParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ratingParams.setMarginStart(dp(context, R.dimen.spacing_1));
        tvRating.setLayoutParams(ratingParams);
        tvRating.setText(movie.getFormattedRating());
        tvRating.setTextColor(ContextCompat.getColor(context, R.color.text_title));
        tvRating.setTextSize(TypedValue.COMPLEX_UNIT_PX, sp(context, R.dimen.text_sm));
        tvRating.setTypeface(null, Typeface.BOLD);

        titleRow.addView(tvTitle);
        titleRow.addView(starIcon);
        titleRow.addView(tvRating);

        TextView tvSubtitle = new TextView(context);
        LinearLayout.LayoutParams subParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subParams.topMargin = dp(context, R.dimen.spacing_2);
        tvSubtitle.setLayoutParams(subParams);
        tvSubtitle.setText(movie.getReleaseYear());
        tvSubtitle.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
        tvSubtitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, sp(context, R.dimen.text_xs));
        tvSubtitle.setLetterSpacing(0.05f);

        TextView tvDesc = new TextView(context);
        LinearLayout.LayoutParams descParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        descParams.topMargin = dp(context, R.dimen.spacing_2);
        tvDesc.setLayoutParams(descParams);
        tvDesc.setText(movie.getOverview());
        tvDesc.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
        tvDesc.setTextSize(TypedValue.COMPLEX_UNIT_PX, sp(context, R.dimen.text_sm));
        tvDesc.setMaxLines(3);
        tvDesc.setEllipsize(TextUtils.TruncateAt.END);

        textContainer.addView(titleRow);
        textContainer.addView(tvSubtitle);
        textContainer.addView(tvDesc);

        rowLayout.addView(imageCard);
        rowLayout.addView(textContainer);

        if (listener != null) {
            rowLayout.setOnClickListener(v -> listener.onMovieClick(movie));
        }
        return rowLayout;
    }

    private static int dp(Context context, int dimenRes) {
        return context.getResources().getDimensionPixelSize(dimenRes);
    }

    /** Tamaño de texto (sp) definido como dimen, en px ya escalado por fuente. */
    private static float sp(Context context, int dimenRes) {
        return context.getResources().getDimension(dimenRes);
    }
}
