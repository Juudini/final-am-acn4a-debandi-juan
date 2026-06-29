package com.example.parcial_2_am_acn4a_debandi_juan.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.widget.Button;

import androidx.core.content.ContextCompat;

import com.example.parcial_2_am_acn4a_debandi_juan.AccountActivity;
import com.example.parcial_2_am_acn4a_debandi_juan.CategoriesActivity;
import com.example.parcial_2_am_acn4a_debandi_juan.MainActivity;
import com.example.parcial_2_am_acn4a_debandi_juan.R;
import com.example.parcial_2_am_acn4a_debandi_juan.SigninActivity;
import com.example.parcial_2_am_acn4a_debandi_juan.WatchlistActivity;

public final class BottomNavbarHelper {

    public static final int TAB_HOME = 0;
    public static final int TAB_WATCHLIST = 1;
    public static final int TAB_CATEGORIES = 2;
    public static final int TAB_ACCOUNT = 3;
    public static final int TAB_NONE = -1;

    private BottomNavbarHelper() {}

    public static void setup(Activity activity, int activeTab) {
        activity.overridePendingTransition(0, 0);

        Button btnHome = activity.findViewById(R.id.bottomNavbar_BtnHome);
        Button btnWatchlist = activity.findViewById(R.id.bottomNavbar_BtnWatchlist);
        Button btnCategories = activity.findViewById(R.id.bottomNavbar_BtnCategories);
        Button btnAccount = activity.findViewById(R.id.bottomNavbar_BtnAccount);

        btnHome.setOnClickListener(v -> {
            if (activeTab != TAB_HOME) {
                Intent intent = new Intent(activity, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                activity.startActivity(intent);
                activity.overridePendingTransition(0, 0);
                activity.finish();
            }
        });

        btnWatchlist.setOnClickListener(v -> {
            if (activeTab != TAB_WATCHLIST) {
                if (AuthService.isLoggedIn()) {
                    Intent intent = new Intent(activity, WatchlistActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    activity.startActivity(intent);
                } else {
                    activity.startActivity(new Intent(activity, SigninActivity.class));
                }
                activity.overridePendingTransition(0, 0);
                if (activeTab != TAB_HOME) activity.finish();
            }
        });

        btnCategories.setOnClickListener(v -> {
            if (activeTab != TAB_CATEGORIES) {
                Intent intent = new Intent(activity, CategoriesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                activity.startActivity(intent);
                activity.overridePendingTransition(0, 0);
                if (activeTab != TAB_HOME) activity.finish();
            }
        });

        btnAccount.setOnClickListener(v -> {
            if (activeTab != TAB_ACCOUNT) {
                if (AuthService.isLoggedIn()) {
                    Intent intent = new Intent(activity, AccountActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    activity.startActivity(intent);
                } else {
                    activity.startActivity(new Intent(activity, SigninActivity.class));
                }
                activity.overridePendingTransition(0, 0);
                if (activeTab != TAB_HOME) activity.finish();
            }
        });

        if (activeTab == TAB_HOME) applySelected(activity, btnHome);
        if (activeTab == TAB_WATCHLIST) applySelected(activity, btnWatchlist);
        if (activeTab == TAB_CATEGORIES) applySelected(activity, btnCategories);
        if (activeTab == TAB_ACCOUNT) applySelected(activity, btnAccount);
    }

    private static void applySelected(Activity activity, Button btn) {
        int bgColor = ContextCompat.getColor(activity, R.color.bg_selected);
        int textColor = ContextCompat.getColor(activity, R.color.text_selected);

        btn.setBackgroundTintList(ColorStateList.valueOf(bgColor));
        btn.setTextColor(textColor);
    }
}
