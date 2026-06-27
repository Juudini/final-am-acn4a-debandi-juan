# 🚀 Plan de Desarrollo - Parcial 2 (Aplicaciones Móviles)

Este repositorio contiene el desarrollo para el segundo parcial de la materia **Aplicaciones Móviles** (Escuela Da Vinci), dictada por el profesor **Sergio Medina**. 

El objetivo de este documento es coordinar las tareas del equipo (**KIRO PLAN**) para asegurar el cumplimiento de todos los requisitos obligatorios y aplicar los agregados técnicos necesarios para alcanzar la nota máxima (10).

---

## ⚠️ 1. Requisitos Críticos de Entrega (Innegociables)
*   **Formato del Repositorio:** El nombre debe estar estrictamente en minúsculas, con los apellidos en orden alfabético y separados por guiones medios: `parcial-2-am-ac[m|t|n]4[a|b|c|d]-[apellido_1]-[apellido_2]`. *Nota: El incumplimiento de este formato resta 2 puntos automáticamente.*
*   **Colaboración en GitHub:** Se auditarán los commits. **Ambos integrantes deben tener una participación activa y equitativa** reflejada en el historial. No se admiten entregas con pocos commits acumulados al final.
*   **Acceso a Cátedra:** Asegurar que el usuario de GitHub `sergiomedinaio` esté invitado como colaborador al repositorio.
*   **Documentación Obligatoria:** Se debe adjuntar un informe en la entrega a través del Campus que incluya:
    *   Enlace al repositorio.
    *   Mocks / Wireframes (Figma o similar) con la descripción de cada pantalla y su flujo de uso esperado.

---

## 🛠️ 2. Desarrollo Mínimo Obligatorio (Base para Nota 4)
Cualquier faltante en esta sección se considera desaprobado de forma innegociable:
*   **Navegación:** Multi-pantalla con flujo lógico y fluido entre las distintas características de la aplicación.
*   **Layouts Requeridos:** Implementación explícita de `ConstraintLayout` y `LinearLayout` (tanto en orientación vertical como horizontal).
*   **Componentes Core:** Uso correcto de `Button` y `TextView`.
*   **Interactividad Dinámica:** Al menos un elemento debe disparar un evento que modifique la interfaz de forma dinámica en relación al objetivo de la app (por ejemplo, añadir elementos interactivos reales dentro de un `ScrollView`).
*   **Contenido Real:** Todos los textos e información deben ser verídicos (sin *Lorem Ipsum*) y estar acompañados de imágenes que enriquezcan el producto gráfico.

---

## 🎯 3. Agregados para Alcanzar la Nota Máxima (Camino al 10)
Para maximizar la valoración del proyecto, implementaremos las siguientes prácticas y tecnologías avanzadas:

### 🎨 Diseño y Organización de Recursos
*   **Cero Hardcoding:** Centralización absoluta de recursos en la carpeta `res/values/`:
    *   `strings.xml` (Textos de la aplicación)
    *   `dimens.xml` (Dimensiones, márgenes y paddings)
    *   `colors.xml` (Paleta de colores limpia y consistente)
*   **Diseño Interfaz:** Ajustes estéticos refinados basados en el feedback de la pre-entrega.

### 🔄 Lógica y Flujo de Datos Avanzado
*   **Pasaje de Datos:** Uso de `Intent` acompañados de `Extras` para el envío de paquetes de datos entre las distintas `Activities`.
*   **Imágenes Remotas:** Descarga y renderizado dinámico de imágenes desde URLs de internet mediante el uso de bibliotecas de terceros como **Glide** o **Picasso**.

### 💻 Calidad de Código y Git
*   **Estrategia de Branching & Commits:** Uso estricto de la convención **Conventional Commits** para mantener un historial limpio y profesional. Ejemplos:
    *   `feat: add product detail activity layout`
    *   `fix: resolve null pointer exception on image loading`
    *   `docs: update wireframes in documentation`

### ☁️ Integración Cloud (Opcional de Alto Impacto)
*   Implementación de servicios de Firebase como **Firebase Auth** (Autenticación de usuarios) y/o **Firestore** (Persistencia de datos reales en tiempo real).

---

## 📋 Próximos Pasos Inmediatos
1. [ ] Validar y corregir el nombre actual del repositorio según la comisión y apellidos.
2. [ ] Confirmar la invitación al usuario `sergiomedinaio`.
3. [ ] Definir la temática sobre la base existente y diseñar los mocks para el informe.
4. [ ] Distribuir tareas para el diseño de XMLs y codificación de lógica en Kotlin/Java.
