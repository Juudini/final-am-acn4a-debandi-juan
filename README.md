# 🎬 Parcial 2 - Cinemapedia

**Autor:** Juan Debandi
**Materia:** Aplicaciones Móviles (ACN4A) - Escuela Da Vinci

Aplicación Android nativa (Java) de películas que consume datos en tiempo real desde **TheMovieDB**. Evolución del Parcial 1 (UI estática) hacia una app multi-pantalla con datos dinámicos, imágenes remotas, autenticación y persistencia en la nube.

## ✨ Funcionalidades

- **Home dinámico:** secciones *Trending* (`/trending/movie/week`) y *New Releases* (`/movie/now_playing`) traídas de la API, con un *hero* destacado.
- **Detalle:** ficha completa de cada película (`/movie/{id}`) con backdrop, rating, géneros, duración y sinopsis. Navegación con `Intent` + `Extras`.
- **Búsqueda:** búsqueda de títulos en vivo (`/search/movie`).
- **Categorías:** listado de géneros (`/genre/movie/list`) y películas por género (`/discover/movie`).
- **Autenticación:** registro e inicio de sesión con **Firebase Auth** (email/password).
- **Watchlist:** guardado de películas por usuario en **Cloud Firestore**. El navegar es libre; guardar requiere sesión iniciada.

## 🛠️ Stack técnico

- **Lenguaje:** Java
- **Red:** Retrofit + Gson + OkHttp (logging interceptor)
- **Imágenes:** Glide
- **Cloud:** Firebase Authentication + Cloud Firestore
- **UI:** ConstraintLayout / LinearLayout, Material Components
- **minSdk:** 24 · **targetSdk:** 36

## 🚀 Cómo ejecutar el proyecto

1. Cloná el repositorio y abrilo en **Android Studio**.

2. **Clave de TheMovieDB:** agregá tu *API Read Access Token* (v4) en `local.properties` (archivo ignorado por git):

   ```properties
   TMDB_API_KEY=tu_token_v4_de_themoviedb
   TMDB_BASE_URL=https://api.themoviedb.org/3/
   TMDB_IMAGE_BASE_URL=https://image.tmdb.org/t/p/w500
   ```

   La clave se inyecta en tiempo de compilación vía `BuildConfig`.

3. **Firebase:** creá un proyecto en [Firebase Console](https://console.firebase.google.com), agregá una app Android con el package `com.example.parcial_2_am_acn4a_debandi_juan` y descargá el archivo `google-services.json` dentro de la carpeta `app/`. Luego:
   - Habilitá **Authentication → Sign-in method → Email/Password**.
   - Creá una **Cloud Firestore Database**.
   - Reglas sugeridas para el watchlist:

     ```
     match /users/{userId}/watchlist/{movieId} {
       allow read, write: if request.auth != null && request.auth.uid == userId;
     }
     ```

4. Sincronizá Gradle y ejecutá en un emulador o dispositivo (API 24+).

> ⚠️ **Importante:** el archivo `google-services.json` es obligatorio para compilar. El plugin `com.google.gms.google-services` está aplicado en `app/build.gradle`, por lo que **sin ese archivo en `app/` el build falla** en la tarea `processDebugGoogleServices`. De la misma forma, sin la `TMDB_API_KEY` en `local.properties` la app compila pero las llamadas a la API devuelven 401.

## 🩹 Solución de problemas

- **El build falla en `processDebugGoogleServices`:** falta `app/google-services.json`. Descargalo desde Firebase Console (app Android con package `com.example.parcial_2_am_acn4a_debandi_juan`) y colocalo en `app/`.
- **Las películas no cargan / error 401:** revisá que `TMDB_API_KEY` en `local.properties` sea el *API Read Access Token* (v4) y volvé a sincronizar Gradle.
- **`Cannot resolve symbol R` o `BuildConfig` en el IDE:** es caché de Android Studio. Hacé **File → Sync Project with Gradle Files** y, si persiste, **File → Invalidate Caches… → Invalidate and Restart**.
- **El login o el watchlist no funcionan:** verificá que en Firebase estén habilitados **Authentication → Email/Password** y **Cloud Firestore Database**, con las reglas del watchlist aplicadas.

## 🌿 Flujo de trabajo (Git)

Modelo de ramas: `main` → `develop` → ramas `feature/*` (una por funcionalidad), integradas a `develop` mediante Pull Requests, siguiendo **Conventional Commits**.

## 🔗 Links

<a href="https://www.linkedin.com/in/juandebandi/"><img alt="LinkedIn" title="LinkedIn" src="https://custom-icon-badges.demolab.com/badge/-LinkedIn-231b2e?style=for-the-badge&logoColor=F8D866&logo=LinkedIn"/></a>
<a href="https://juandebandi.dev/"><img alt="Portfolio" title="Portfolio" src="https://custom-icon-badges.demolab.com/badge/-|Portfolio-1F222E?style=for-the-badge&logoColor=F8D866&logo=link-external"/></a>
<a href="mailto:juudinidev@gmail.com">
<img src="https://custom-icon-badges.demolab.com/badge/-Email-231b2e?style=for-the-badge&logoColor=F8D866&logo=gmail" alt="Email">
</a>
