# Tarea 2: Elementos basicos de interfaz de usuario

---

## Datos de Identificación

- **Nombre:** Carlos Daniel Martinez Martinez
- **Número de boleta:** 2024630459
- **Grupo:** 7CV4
- **Asignatura:** Desarrollo de Aplicaciones Moviles Nativas
- **Fecha de realización:** 25 de Septiembre de 2026

---
## Descripción de la Aplicación

El presente proyecto consiste en un catálogo interactivo diseñado para explorar, identificar y documentar los componentes básicos de una interfaz de usuario móvil. La aplicación se encuentra implementada en tres tecnologías y paradigmas de desarrollo distintos, con el objetivo principal de contrastar sus equivalencias, la estructuración arquitectónica de las vistas y las diferencias inherentes en el flujo de construcción de interfaces:

1. **Android Nativo con Views y XML** (Paradigma imperativo)
2. **Android Nativo con Jetpack Compose** (Paradigma declarativo)
3. **Flutter** (Framework multiplataforma de paradigma declarativo)
   *(Opcional)* 4. **[Indicar la cuarta tecnología, e.g., React Native / .NET MAUI]**

La estructura del proyecto comprende una pantalla principal de navegación y seis secciones temáticas que categorizan diversos elementos de interfaz (Entrada de texto, Botones y acciones, Elementos de selección, Listas y colecciones, Información y retroalimentación, y Contenedores). Adicionalmente, todas las implementaciones cumplen con los requisitos transversales de soporte para modo claro/oscuro del sistema, navegación funcional y documentación en pantalla.

## Instrucciones de Compilación y Ejecución

### 1. Versión Android Nativo (Views y XML) - Directorio `android-views/`

1. Abrir el entorno de desarrollo Android Studio.
2. Seleccionar la opción `File > Open...` y localizar el directorio `android-views`.
3. Aguardar la sincronización del gestor de dependencias Gradle.
4. Seleccionar un emulador configurado o dispositivo físico conectado y ejecutar el proyecto mediante la opción **Run** (Shift + F10).

### 2. Versión Android Nativo (Jetpack Compose) - Directorio `android-compose/`

1. Abrir Android Studio (se requiere una versión actualizada para el correcto soporte de las herramientas de Compose).
2. Seleccionar la opción `File > Open...` y localizar el directorio `android-compose`.
3. Permitir la sincronización del proyecto con Gradle.
4. Compilar y ejecutar la aplicación seleccionando la opción **Run**.

### 3. Versión Flutter - Directorio `flutter/`

1. Verificar la instalación y correcta configuración del SDK de Flutter en las variables de entorno del sistema.
2. Abrir una terminal o línea de comandos y posicionarse en el directorio del proyecto: `cd flutter/`.
3. Descargar y actualizar las dependencias necesarias ejecutando el comando: `flutter pub get`.
4. Compilar y ejecutar la aplicación en un dispositivo o emulador mediante el comando: `flutter run`.

## Tabla de Equivalencias de Componentes

A continuación, se detalla la correspondencia técnica de los componentes implementados en cada una de las tres tecnologías requeridas, clasificados por sección temática.

| Sección / Elemento | Views / XML (Kotlin) | Jetpack Compose | Flutter (Dart) |
| :--- | :--- | :--- | :--- |
| **1. ENTRADA DE TEXTO** | | | |
| Campo simple con etiqueta/hint | `EditText` / `TextInputLayout` | `TextField` (o `OutlinedTextField`) | `TextField` |
| Campo con validación y error | `TextInputLayout` (app:errorEnabled) | `TextField` (isError = true) | `TextFormField` (validator) |
| Campo de contraseña (toggle) | `TextInputLayout` (endIconMode="password_toggle") | `TextField` (VisualTransformation) | `TextField` (obscureText = true) |
| Teclados específicos | `EditText` (inputType) | `TextField` (keyboardOptions) | `TextField` (keyboardType) |
| Campo multilínea | `EditText` (inputType="textMultiLine") | `TextField` (minLines, maxLines) | `TextField` (maxLines: null) |
| Sugerencias automáticas | `AutoCompleteTextView` | `ExposedDropdownMenuBox` | `Autocomplete<T>` |
| Barra de búsqueda | `SearchView` | `SearchBar` | `SearchBar` / `TextField` con íconos |
| **2. BOTONES Y ACCIONES** | | | |
| Botón relleno | `Button` | `Button` | `ElevatedButton` / `FilledButton` |
| Botón con contorno | `MaterialButton` (style="...OutlinedButton") | `OutlinedButton` | `OutlinedButton` |
| Botón de solo texto | `Button` (style="...TextButton") | `TextButton` | `TextButton` |
| Botón con ícono | `MaterialButton` (app:icon) | `IconButton` / `Button` + `Icon` | `IconButton` / `ElevatedButton.icon` |
| Botón de acción flotante (FAB) | `FloatingActionButton` | `FloatingActionButton` | `FloatingActionButton` |
| FAB extendido | `ExtendedFloatingActionButton` | `ExtendedFloatingActionButton` | `FloatingActionButton.extended` |
| Botón de alternancia (toggle) | `MaterialButtonToggleGroup` | `SegmentedButton` | `ToggleButtons` / `SegmentedButton` |
| Estado deshabilitado / carga | `Button` (isEnabled=false) | `Button` (enabled=false) | `ElevatedButton` (onPressed: null) |
| **3. ELEMENTOS DE SELECCIÓN** | | | |
| Casilla de verificación | `CheckBox` | `Checkbox` | `Checkbox` |
| Casilla (estado indeterminado) | `MaterialCheckBox` (state="indeterminate") | `TriStateCheckbox` | `Checkbox` (tristate: true) |
| Botones de opción | `RadioGroup` + `RadioButton` | `Column` + `RadioButton` | `Radio` / `RadioListTile` |
| Interruptor (Switch) | `Switch` / `SwitchCompat` | `Switch` | `Switch` |
| Deslizador de valor único | `Slider` | `Slider` | `Slider` |
| Deslizador de rango | `RangeSlider` | `RangeSlider` | `RangeSlider` |
| Selector de fecha y hora | `DatePickerDialog` / `TimePickerDialog` | `DatePicker` / `TimePicker` | `showDatePicker()` / `showTimePicker()` |
| Chips de filtro | `ChipGroup` + `Chip` | `FilterChip` | `FilterChip` |
| **4. LISTAS Y COLECCIONES** | | | |
| Lista vertical | `RecyclerView` + `LinearLayoutManager` | `LazyColumn` | `ListView.builder` |
| Cuadrícula | `RecyclerView` + `GridLayoutManager` | `LazyVerticalGrid` | `GridView.builder` |
| Encabezados de sección | `RecyclerView` (Múltiples ViewTypes) | `LazyColumn` (item + items) | `ListView` con lógica condicional |
| Deslizar para eliminar | `ItemTouchHelper` | `SwipeToDismissBox` | `Dismissible` |
| Arrastrar para actualizar | `SwipeRefreshLayout` | `PullToRefreshContainer` | `RefreshIndicator` |
| Estado vacío | XML Layout condicional (View.GONE) | Estructura de control `if/else` | Estructura de control `if/else` |
| Pestañas deslizables | `TabLayout` + `ViewPager2` | `TabRow` + `HorizontalPager` | `TabBar` + `TabBarView` |
| **5. INFORMACIÓN Y RETROALIMENTACIÓN** | | | |
| Textos con estilos | `TextView` (textAppearance) | `Text` (style) | `Text` (style: TextStyle) |
| Imagen local | `ImageView` (src) | `Image` (painterResource) | `Image.asset` |
| Imagen URL | `ImageView` + librería (Glide/Picasso) | `AsyncImage` (Librería Coil) | `Image.network` |
| Indicador de progreso | `ProgressBar` | `LinearProgressIndicator` | `LinearProgressIndicator` |
| Mensaje emergente (Toast) | `Toast.makeText().show()` | `Toast.makeText().show()` * | `Fluttertoast` / `SnackBar` |
| Mensaje con acción (Snackbar) | `Snackbar.make().show()` | `SnackbarHost` / `Scaffold` | `ScaffoldMessenger.showSnackBar` |
| Diálogo de confirmación | `AlertDialog.Builder` | `AlertDialog` | `showDialog` + `AlertDialog` |
| Hoja inferior (Bottom sheet) | `BottomSheetDialog` | `ModalBottomSheet` | `showModalBottomSheet()` |
| Tarjeta, separador, badge | `CardView`, `View`, `BadgeDrawable` | `Card`, `HorizontalDivider`, `Badge` | `Card`, `Divider`, `Badge` |
| **6. CONTENEDORES Y ESTRUCTURA** | | | |
| Distribución (Fila/Columna) | `LinearLayout` (Horizontal/Vertical) | `Row`, `Column` | `Row`, `Column` |
| Distribución superpuesta | `FrameLayout` | `Box` | `Stack` |
| Desplazamiento vertical | `ScrollView` / `NestedScrollView` | `Modifier.verticalScroll` | `SingleChildScrollView` |
| Barra superior | `MaterialToolbar` / `ActionBar` | `TopAppBar` | `AppBar` |
| Barra / Menú de navegación | `BottomNavigationView` / `DrawerLayout` | `BottomAppBar` / `ModalNavigationDrawer` | `BottomNavigationBar` / `Drawer` |
| Restricciones / Pesos | `ConstraintLayout` / `layout_weight` | `ConstraintLayout` / `Modifier.weight` | `Expanded` / `Flexible` |

*\* Nota sobre el componente Toast en Compose: Debido a la naturaleza puramente declarativa del framework, se utilizó la interoperabilidad con el sistema tradicional de Android invocando la clase `Toast` dentro del contexto actual.*

## Capturas de Pantalla

A continuación se presentan las evidencias gráficas del correcto funcionamiento de las seis secciones solicitadas, correspondientes a cada tecnología implementada. Las imágenes se encuentran referenciadas desde el directorio `docs/`.

### 1. Android Nativo (Views / XML)

| Pantalla Principal | 1. Textos | 2. Botones | 3. Selección | 4. Listas | 5. Retroalimentación | 6. Estructura |
| :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| [Imagen] | [Imagen] | [Imagen] | [Imagen] | [Imagen] | [Imagen] | [Imagen] |

### 2. Android Nativo (Jetpack Compose)

| Pantalla Principal | 1. Textos | 2. Botones | 3. Selección | 4. Listas | 5. Retroalimentación | 6. Estructura |
| :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| [Imagen] | [Imagen] | [Imagen] | [Imagen] | [Imagen] | [Imagen] | [Imagen] |

### 3. Flutter

| Pantalla Principal | 1. Textos | 2. Botones | 3. Selección | 4. Listas | 5. Retroalimentación | 6. Estructura |
| :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| [Imagen] | [Imagen] | [Imagen] | [Imagen] | [Imagen] | [Imagen] | [Imagen] |

## Reflexión Final

*Nota: Sustituir el texto entre corchetes con el análisis personal y fundamentado correspondiente a la práctica realizada.*

**1. Evaluación de la eficiencia en la construcción de la interfaz:**
[Análisis sobre qué tecnología permitió un desarrollo más ágil. Comparar el tiempo invertido en el paradigma imperativo frente al declarativo, considerando aspectos como la recarga en caliente (hot reload) y la curva de aprendizaje.]

**2. Análisis de la legibilidad del código generado:**
[Evaluación sobre la claridad de las estructuras de código. Mencionar, por ejemplo, si la separación estricta de responsabilidades (XML y Kotlin) resulta más comprensible, o si la centralización declarativa (Compose/Flutter) reduce la complejidad, abordando también posibles inconvenientes como el anidamiento profundo.]

**3. Dificultades técnicas encontradas durante la implementación:**
* **Views / XML:** [Describir los principales obstáculos técnicos enfrentados, e.g., configuración de adaptadores complejos, sincronización de estado, etc.]
* **Jetpack Compose:** [Describir los retos específicos del framework, e.g., comprensión de la recomposición, manejo de estados mutables, sistema de modificadores.]
* **Flutter:** [Describir las barreras encontradas, e.g., adaptación al lenguaje Dart, manejo de desbordamientos visuales (overflows), gestión del árbol de widgets.]

**4. Conclusión y preferencia tecnológica:**
[Conclusión sobre la tecnología que resulta más viable para proyectos futuros, justificando la elección con base en la experiencia práctica adquirida durante el desarrollo del catálogo.]

## Referencias Consultadas

* Android Developers. (2024). *Build a UI with layout editor*. Recuperado de https://developer.android.com/studio/write/layout-editor
* Android Developers. (2024). *Jetpack Compose basics*. Recuperado de https://developer.android.com/jetpack/compose/tutorial
* Flutter. (2024). *Introduction to widgets*. Recuperado de https://docs.flutter.dev/ui/widgets/basics
* [Incluir fuentes adicionales utilizadas durante el desarrollo, respetando el formato de citación APA.]