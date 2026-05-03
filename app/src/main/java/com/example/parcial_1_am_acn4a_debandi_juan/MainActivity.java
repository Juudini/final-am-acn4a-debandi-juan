package com.example.parcial_1_am_acn4a_debandi_juan;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button btnPlay = findViewById(R.id.heroSection_BtnPlay);

        trendingMoviesContainer = findViewById(R.id.trendingMoviesContainer);

        String[][] movies = {
                {"Super Mario Galaxy", "FANTASY • 2026", "img_mario_galaxy"},
                {"Kimetsu no Yaiba", "ANIME • 2026", "img_kimetsu_no_yaiba"},
                {"Zootropolis 2", "FANTASY • 2026", "img_zootropolis2"}
        };

        for (String[] movie : movies) {
            createMovieCard(movie[0], movie[1], movie[2]);
        }

        btnPlay.setOnClickListener(v -> {
            btnPlay.setText("LOADING...");
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void createMovieCard(String title, String subtitle, String imageName) {
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
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tvTitle.setTypeface(null, Typeface.BOLD);
        tvTitle.setMaxLines(1);

        TextView tvSubtitle = new TextView(this);
        tvSubtitle.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tvSubtitle.setText(subtitle);
        tvSubtitle.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        tvSubtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        tvSubtitle.setLetterSpacing(0.05f);

        cardLayout.addView(imageCard);
        cardLayout.addView(tvTitle);
        cardLayout.addView(tvSubtitle);
        trendingMoviesContainer.addView(cardLayout);
    }
}