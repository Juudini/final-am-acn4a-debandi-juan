package com.example.parcial_2_am_acn4a_debandi_juan.util;

import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.parcial_2_am_acn4a_debandi_juan.BuildConfig;
import com.example.parcial_2_am_acn4a_debandi_juan.R;

/**
 * Helper centralizado para cargar imágenes remotas de TheMovieDB con Glide.
 * Construye la URL completa a partir del path relativo (ej. "/abc.jpg").
 */
public final class ImageLoader {

    private ImageLoader() {
    }

    /** Construye la URL absoluta de una imagen de TMDB, o null si no hay path. */
    @Nullable
    public static String buildImageUrl(@Nullable String path) {
        if (path == null || path.trim().isEmpty()) {
            return null;
        }
        return BuildConfig.TMDB_IMAGE_BASE_URL + path;
    }

    /** Carga el poster/backdrop indicado en el ImageView con placeholder y fallback de error. */
    public static void load(ImageView target, @Nullable String path) {
        String url = buildImageUrl(path);
        Glide.with(target.getContext())
                .load(url)
                .placeholder(R.drawable.img_poster_placeholder)
                .error(R.drawable.img_poster_placeholder)
                .centerCrop()
                .into(target);
    }
}
