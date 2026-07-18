package com.example.final_am_acn4a_debandi_juan.ui.common.navigation;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;

import androidx.core.content.ContextCompat;

import com.example.final_am_acn4a_debandi_juan.R;
import com.example.final_am_acn4a_debandi_juan.ui.account.AccountActivity;
import com.example.final_am_acn4a_debandi_juan.ui.categories.CategoriesActivity;
import com.example.final_am_acn4a_debandi_juan.ui.home.MainActivity;
import com.example.final_am_acn4a_debandi_juan.ui.watchlist.WatchlistActivity;
import com.google.android.material.button.MaterialButton;

public final class BottomNavbarHelper {

    public static final int TAB_HOME = 0;
    public static final int TAB_WATCHLIST = 1;
    public static final int TAB_CATEGORIES = 2;
    public static final int TAB_ACCOUNT = 3;
    public static final int TAB_NONE = -1;

    private BottomNavbarHelper() {}

    public static void setup(Activity activity, int activeTab) {
        activity.overridePendingTransition(0, 0);

        MaterialButton btnHome = activity.findViewById(R.id.bottomNavbar_BtnHome);
        MaterialButton btnWatchlist = activity.findViewById(R.id.bottomNavbar_BtnWatchlist);
        MaterialButton btnCategories = activity.findViewById(R.id.bottomNavbar_BtnCategories);
        MaterialButton btnAccount = activity.findViewById(R.id.bottomNavbar_BtnAccount);

        if (btnHome == null || btnWatchlist == null || btnCategories == null || btnAccount == null) {
            return;
        }

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
                Intent intent = new Intent(activity, WatchlistActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                activity.startActivity(intent);
                activity.overridePendingTransition(0, 0);
                if (activeTab != TAB_HOME) {
                    activity.finish();
                }
            }
        });

        btnCategories.setOnClickListener(v -> {
            if (activeTab != TAB_CATEGORIES) {
                Intent intent = new Intent(activity, CategoriesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                activity.startActivity(intent);
                activity.overridePendingTransition(0, 0);
                if (activeTab != TAB_HOME) {
                    activity.finish();
                }
            }
        });

        btnAccount.setOnClickListener(v -> {
            if (activeTab != TAB_ACCOUNT) {
                Intent intent = new Intent(activity, AccountActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                activity.startActivity(intent);
                activity.overridePendingTransition(0, 0);
                if (activeTab != TAB_HOME) {
                    activity.finish();
                }
            }
        });

        if (activeTab == TAB_HOME) {
            applySelected(activity, btnHome);
        }
        if (activeTab == TAB_WATCHLIST) {
            applySelected(activity, btnWatchlist);
        }
        if (activeTab == TAB_CATEGORIES) {
            applySelected(activity, btnCategories);
        }
        if (activeTab == TAB_ACCOUNT) {
            applySelected(activity, btnAccount);
        }
    }

    private static void applySelected(Activity activity, MaterialButton btn) {
        int bgColor = ContextCompat.getColor(activity, R.color.bg_selected);
        int textColor = ContextCompat.getColor(activity, R.color.text_selected);
        int iconColor = ContextCompat.getColor(activity, R.color.icon_selected);

        btn.setBackgroundTintList(ColorStateList.valueOf(bgColor));
        btn.setTextColor(textColor);
        btn.setIconTint(ColorStateList.valueOf(iconColor));
    }
}
