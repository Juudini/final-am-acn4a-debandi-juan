package com.example.parcial_2_am_acn4a_debandi_juan;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {
    private LinearLayout trendingMoviesContainer;
    private LinearLayout newReleasesContainer;
    private ScrollView mainScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        mainScrollView = findViewById(R.id.mainScrollView);
        LinearLayout topHeader = findViewById(R.id.topHeader);

        mainScrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {

            if (scrollY > 120) {
                topHeader.setBackgroundColor(ContextCompat.getColor(this, R.color.bg_shadow));
            } else {
                topHeader.setBackgroundColor(Color.TRANSPARENT);
            }

        });
        Button btnPlay = findViewById(R.id.heroSection_BtnPlay);
        btnPlay.setOnClickListener(v -> {
            btnPlay.setText("LOADING...");
        });

        trendingMoviesContainer = findViewById(R.id.trendingMoviesContainer);
        newReleasesContainer = findViewById(R.id.newReleasesContainer);

        String[][] trendingMovies = {
                {"Super Mario Galaxy", "FANTASY • 2026", "img_mario_galaxy"},
                {"Kimetsu no Yaiba", "ANIME • 2026", "img_kimetsu_no_yaiba"},
                {"Zootropolis 2", "FANTASY • 2026", "img_zootropolis2"}
        };

        String[][] newReleasesData = {
                {"Top Gun: Maverick", "8.1", "ACTION • 2H 11M • 2026", "Released in 1986, Top Gun is a cult film that has crossed the decades. Featuring young fighter pilots and full of action scenes in flight, it became the reference for aviation films. It is also the film that will make Tom Cruise's career take off, forever associated with his character nicknamed Maverick", "img_top_gun_maverick"},
                {"Apex", "9.0", "ACTION - THRILLER • 1H 36M • 2026", "A grieving woman pushing her limits on a solo adventure in the Australian wild is ensnared in a twisted game with a cunning killer who thinks she's prey.", "img_apex"},
                {"Case 137", "7.4", "CRIME - DRAMA • 1H 55M • 2026", "Stéphanie, a police officer working for Internal Affairs, is assigned to a case involving a young man severely wounded during a tense and chaotic demonstration in Paris. While she finds no evidence of illegitimate police violence, the case takes a personal turn when she discovers the victim is from her hometown.", "img_case137"}
        };

        for (String[] movie : trendingMovies) {
            createMovieCard(movie[0], movie[1], movie[2], trendingMoviesContainer);
        }

        for (String[] release : newReleasesData) {
            createReleaseListCard(release[0], release[1], release[2], release[3], release[4], newReleasesContainer);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void createMovieCard(String title, String subtitle, String imageName, LinearLayout targetContainer) {
        int cardWidth = getResources().getDimensionPixelSize(R.dimen.movie_card_width);
        int cardHeight = getResources().getDimensionPixelSize(R.dimen.movie_card_height);
        int marginEnd = getResources().getDimensionPixelSize(R.dimen.spacing_4);
        int marginBottom = getResources().getDimensionPixelSize(R.dimen.spacing_2);
        int cornerRadius = getResources().getDimensionPixelSize(R.dimen.radius_md);

        LinearLayout cardLayout = new LinearLayout(this);
        cardLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(cardWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, marginEnd, 0);
        cardLayout.setLayoutParams(params);

        MaterialCardView imageCard = new MaterialCardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, cardHeight);
        cardParams.bottomMargin = marginBottom;
        imageCard.setLayoutParams(cardParams);
        imageCard.setRadius(cornerRadius);
        imageCard.setStrokeWidth(0);
        imageCard.setCardBackgroundColor(Color.TRANSPARENT);

        ImageView poster = new ImageView(this);
        poster.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        poster.setScaleType(ImageView.ScaleType.CENTER_CROP);

        int resourceId = getResources().getIdentifier(imageName, "drawable", getPackageName());
        if(resourceId != 0) {
            poster.setImageResource(resourceId);
        } else {
            poster.setBackgroundColor(ContextCompat.getColor(this, R.color.bg_navbar));
        }
        imageCard.addView(poster);

        TextView tvTitle = new TextView(this);
        tvTitle.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tvTitle.setText(title);
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.text_title));
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tvTitle.setTypeface(null, Typeface.BOLD);
        tvTitle.setMaxLines(1);

        TextView tvSubtitle = new TextView(this);
        tvSubtitle.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tvSubtitle.setText(subtitle);
        tvSubtitle.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        tvSubtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        tvSubtitle.setLetterSpacing(0.05f);

        cardLayout.addView(imageCard);
        cardLayout.addView(tvTitle);
        cardLayout.addView(tvSubtitle);
        targetContainer.addView(cardLayout);
    }

    private void createReleaseListCard(String title, String rating, String subtitle, String description, String imageName, LinearLayout targetContainer) {

        int posterWidth = getResources().getDimensionPixelSize(R.dimen.newRelease_card_width);
        int posterHeight = getResources().getDimensionPixelSize(R.dimen.newRelease_card_height);
        int marginStart = getResources().getDimensionPixelSize(R.dimen.spacing_4);
        int marginBottom = getResources().getDimensionPixelSize(R.dimen.spacing_4);
        int cornerRadius = getResources().getDimensionPixelSize(R.dimen.radius_md);

        LinearLayout rowLayout = new LinearLayout(this);
        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rowParams.bottomMargin = marginBottom;
        rowLayout.setLayoutParams(rowParams);

        MaterialCardView imageCard = new MaterialCardView(this);
        imageCard.setLayoutParams(new LinearLayout.LayoutParams(posterWidth, posterHeight));
        imageCard.setRadius(cornerRadius);
        imageCard.setStrokeWidth(0);
        imageCard.setCardBackgroundColor(Color.TRANSPARENT);

        ImageView poster = new ImageView(this);
        poster.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        poster.setScaleType(ImageView.ScaleType.CENTER_CROP);

        int resourceId = getResources().getIdentifier(imageName, "drawable", getPackageName());
        if(resourceId != 0) {
            poster.setImageResource(resourceId);
        } else {
            poster.setBackgroundColor(ContextCompat.getColor(this, R.color.bg_navbar));
        }
        imageCard.addView(poster);

        LinearLayout textContainer = new LinearLayout(this);
        textContainer.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        textParams.setMarginStart(marginStart);
        textParams.gravity = android.view.Gravity.CENTER_VERTICAL;
        textContainer.setLayoutParams(textParams);

        LinearLayout titleRow = new LinearLayout(this);
        titleRow.setOrientation(LinearLayout.HORIZONTAL);
        titleRow.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        titleRow.setGravity(android.view.Gravity.CENTER_VERTICAL);

        TextView tvTitle = new TextView(this);
        tvTitle.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
        tvTitle.setText(title);
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.text_title));
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        tvTitle.setTypeface(null, Typeface.BOLD);

        ImageView starIcon = new ImageView(this);
        int starSize = getResources().getDimensionPixelSize(R.dimen.spacing_4);
        starIcon.setLayoutParams(new LinearLayout.LayoutParams(starSize, starSize));
        starIcon.setImageResource(R.drawable.ic_star_rate);
        starIcon.setColorFilter(ContextCompat.getColor(this, R.color.bg_selected));

        TextView tvRating = new TextView(this);
        LinearLayout.LayoutParams ratingParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ratingParams.setMarginStart(getResources().getDimensionPixelSize(R.dimen.spacing_1));
        tvRating.setLayoutParams(ratingParams);
        tvRating.setText(rating);
        tvRating.setTextColor(ContextCompat.getColor(this, R.color.text_title));
        tvRating.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        tvRating.setTypeface(null, Typeface.BOLD);

        titleRow.addView(tvTitle);
        titleRow.addView(starIcon);
        titleRow.addView(tvRating);

        TextView tvSubtitle = new TextView(this);
        LinearLayout.LayoutParams subParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subParams.topMargin = getResources().getDimensionPixelSize(R.dimen.spacing_2);
        tvSubtitle.setLayoutParams(subParams);
        tvSubtitle.setText(subtitle);
        tvSubtitle.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        tvSubtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        tvSubtitle.setLetterSpacing(0.05f);

        TextView tvDesc = new TextView(this);
        LinearLayout.LayoutParams descParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        descParams.topMargin = getResources().getDimensionPixelSize(R.dimen.spacing_2);
        tvDesc.setLayoutParams(descParams);
        tvDesc.setText(description);
        tvDesc.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        tvDesc.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        tvDesc.setMaxLines(3);
        tvDesc.setEllipsize(android.text.TextUtils.TruncateAt.END);

        textContainer.addView(titleRow);
        textContainer.addView(tvSubtitle);
        textContainer.addView(tvDesc);

        rowLayout.addView(imageCard);
        rowLayout.addView(textContainer);

        targetContainer.addView(rowLayout);
    }
}