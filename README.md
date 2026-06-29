# 🎬 Parcial 2 - App de Películas (Cinemapedia )
**Autor:** Juan Debandi  
**Materia:** Aplicaciones Móviles (ACN4A)

Este proyecto es una aplicación móvil de películas y series desarrollado de forma nativa para Android utilizando Java y XML con Gradle. Integra la API de TMDb para el catálogo de películas y Firebase (Auth y Firestore) para la autenticación de usuarios y la gestión de la lista de seguimiento (Watchlist).

## 🎨 Diseño en Figma
El desarrollo de esta interfaz gráfica está basado en el prototipo de alta fidelidad diseñado de manera previa, respetando paletas de colores, espaciados y jerarquías tipográficas.

🔗 **[Ver el diseño en Figma](https://www.figma.com/design/f6VjxW4JTYsKf2DLqvNBJo/parcial-1-am-acn4a-debandi_juan?node-id=1-2&t=kFuZIx6OQgKYPo1l-1)**

## 📱 Arquitectura de la Pantalla y Experiencia de Usuario (UX)
La pantalla principal fue estructurada siguiendo un estándar de diseño y priorizando la inmersión y facilidad de navegación.

* **Top Header (Sticky & Dinámico):** Contiene el título de la vista y el search.
* **Hero Section (Estreno Principal):** Ocupa la parte superior empleando un diseño con un alto contraste para los textos de la sinopsis y el action button ("Add to Watchlist").
* **Trending Now (Scroll Horizontal):** Un ScrollView horizontal que permite ver el contenido destacado rápidamente sin sacrificar demasiado espacio vertical en la pantalla.
* **New Releases (Lista Vertical):** Una lista vertical que provee mayor detalle por cada película (título, calificación, género, duración y una breve sinopsis).
* **Bottom NavBar:** Barra de navegación abajo para facilitar el uso.

---

## Credenciales (`local.properties`)
Para poder hacer fetchs a la API de **TheMovieDB (TMDb)**, es necesario configurar las claves de la API en el archivo `local.properties` en la raíz del proyecto.

1. Crea o edita el archivo `local.properties` en la raíz de tu proyecto.
2. Agrega las siguientes variables con tus credenciales:

```properties
# Credenciales API de TheMovieDB
TMDB_API_KEY=TU_API_KEY_AQUI
TMDB_BASE_URL=https://api.themoviedb.org/3/
TMDB_IMAGE_BASE_URL=https://image.tmdb.org/t/p/w500
```
---

## Firebase (Auth & Firestore)
La aplicación utiliza Firebase para la autenticación de usuarios y para guardar la lista de seguimiento (Watchlist) en tiempo real con Firestore. Para configurar Firebase:

### 1. Registrar la App en Firebase
1. Ve a la [Consola de Firebase](https://console.firebase.google.com/).
2. Crea un nuevo proyecto (o selecciona uno existente).
3. Registra una nueva aplicación Android en el proyecto usando el nombre de paquete:
   `com.example.parcial_2_am_acn4a_debandi_juan`
4. Descarga el archivo `google-services.json` y colócalo en el directorio `/app/` del proyecto.

### 2. Configurar Firebase Authentication
1. En la consola de Firebase, ve a la sección de **Authentication**.
2. Habilitar el proveedor de inicio de sesión **Correo electrónico/contraseña** (Email/Password).
3. Esto permitirá el registro de usuarios (`SignupActivity`) y el inicio de sesión (`SigninActivity`).

### 3. Configurar Cloud Firestore
1. Ve a la sección de **Firestore Database** en la consola.
2. Configura las reglas de seguridad para que solo los usuarios autenticados puedan leer y escribir en su propia lista de seguimiento. Utiliza las siguientes reglas:
   ```javascript
   rules_version = '2';
   service cloud.firestore {
     match /databases/{database}/documents {
       match /users/{userId}/watchlist/{movieId} {
         allow read, write: if request.auth != null && request.auth.uid == userId;
       }
     }
   }
   ```
3. La aplicación estructurará los datos guardando cada película en la ruta `/users/{userId}/watchlist/{movieId}` en base al UID del usuario autenticado.

---

## Dependencias Utilizadas
El proyecto utiliza las siguientes dependencias especificadas en `gradle/libs.versions.toml` y aplicadas en `app/build.gradle`:

### Red y Consumo de APIs (TMDb)
| Dependencia | Versión | Descripción |
|---|---|---|
| `com.squareup.retrofit2:retrofit` | `2.11.0` | Cliente HTTP autodeclarativo para consumir la API REST de TMDb. |
| `com.squareup.retrofit2:converter-gson` | `2.11.0` | Conversor de Retrofit para serializar y deserializar JSON utilizando Gson. |
| `com.squareup.okhttp3:logging-interceptor` | `4.12.0` | Interceptor para registrar (loggear) en Logcat las peticiones HTTP y sus respuestas. |

### Carga de Imágenes
| Dependencia | Versión | Descripción |
|---|---|---|
| `com.github.bumptech.glide:glide` | `4.16.0` | Framework para la descarga y visualización eficiente de imágenes y pósteres de películas. |
| `com.github.bumptech.glide:compiler` | `4.16.0` | Procesador de anotaciones para la generación del código de Glide. |

### Firebase
| Dependencia | Versión | Descripción |
|---|---|---|
| `com.google.firebase:firebase-bom` | `33.7.0` | Firebase Bill of Materials (BOM) para gestionar las versiones compatibles de Firebase. |
| `com.google.firebase:firebase-auth` | *Gestionada* | Autenticación de usuarios (registro, login y logout). |
| `com.google.firebase:firebase-firestore` | *Gestionada* | Base de datos NoSQL en la nube para sincronizar la Watchlist por usuario. |

---

## 🚀 Cómo ejecutar el proyecto
1. Clonar repositorio origin main.
2. Configurar el archivo [local.properties] con las credenciales de TMDb como se detalló arriba.
3. Colocar el archivo `google-services.json` configurado de tu Firebase Console en la carpeta app/.
4. Abrir el proyecto desde **Android Studio**.
5. Esperar la sincronización automática de Gradle.
6. Ejecutar la aplicación en un emulador o dispositivo físico (API 24 o superior).

## 🔗 Links

<a href="https://www.linkedin.com/in/juandebandi/"><img alt="LinkedIn" title="LinkedIn" src="https://custom-icon-badges.demolab.com/badge/-LinkedIn-231b2e?style=for-the-badge&logoColor=F8D866&logo=LinkedIn"/></a>
<a href="https://juandebandi.dev/"><img alt="Portfolio" title="Portfolio" src="https://custom-icon-badges.demolab.com/badge/-|Portfolio-1F222E?style=for-the-badge&logoColor=F8D866&logo=link-external"/></a>
<a href="mailto:juudinidev@gmail.com">
<img src="https://custom-icon-badges.demolab.com/badge/-Email-231b2e?style=for-the-badge&logoColor=F8D866&logo=gmail" alt="Email">
</a>
