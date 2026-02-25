# CHANGELOG - OSSA Control

Registro de cambios del proyecto. Cada integrante documenta aquí lo que va haciendo.

---

## Semana 1 - Setup inicial

### Arturo - Fecha: 08/02/2026
- ✅ Creado proyecto en Android Studio
- ✅ Configurado Firebase (Authentication + Firestore)
- ✅ Conectado proyecto con Firebase (google-services.json)
- ✅ Añadidas dependencias de Firebase en build.gradle
- ✅ Subido proyecto a GitHub
- ✅ Añadidos compañeros al repositorio

### [Alejandra] - Fecha: 09/02/2026
- ✅ Variables actualizadas del sdk (compile y target), manifest y build.gradle para que no
  explote la app al abrir
- ✅ MainActivity editado para llamar a la AppRoot
- ✅ AppRoot creado
- ✅ Nuevas carpetas ui + ui.screens creadas
- ✅ Añadidas todas las screens para el login: HomeScreen, LoginScreen y SignUpScreen

### [Alberto] - Fecha: 10-11/02/2026
- ✅ Implementada arquitectura MVVM (AuthViewModel + AdminViewModel)
- ✅ Sistema de gestión de roles (admin/alumno)
- ✅ AppRoot2.kt con navegación basada en roles
- ✅ AdminHomeScreen con listado de alumnos en tiempo real
- ✅ AddStudentScreen para dar de alta alumnos
- ✅ StudentDetailScreen con detalle del alumno
- ✅ StudentHomeScreen para perfil del alumno
- ✅ Sistema de asistencia con simulación de escaneo QR

---

## Semana 2 - Corrección de errores y mejoras

### [Arturo] - Fecha: 14/02/2026
- ✅ Configurado repositorio JitPack en settings.gradle.kts (necesario para dependencias externas)
- ✅ Sustituida librería QR rota (alexzh:qrcode-compose) por ZXing de Google (com.google.zxing:core:3.5.3)
- ✅ Añadida dependencia material-icons-extended (necesaria para el icono QrCode)
- ✅ StudentDetailScreen.kt reescrito completo:
    - ✅ Generación de QR funcional con ZXing (función generarCodigoQR)
    - ✅ Selector de cinturones ampliado de 3 a 5 (Blanco, Azul, Morado, Marrón, Negro)
    - ✅ Cada cinturón muestra su color real en el selector
    - ✅ Añadido selector de stripes/grados (0-4) por cinturón
    - ✅ Guardado de cinturón y grados en Firebase
- ✅ Documentación y comentarios añadidos en todos los archivos modificados
- ✅ App probada y funcionando en emulador
- - ✅ Corregida carga de datos en StudentDetailScreen (añadido LaunchedEffect para obtener alumnos)

### [Alejandra] - Fecha: 25/02/2026
- ✅ R03F01T01: Subcolección de asistencias. Guarda a qué día y a qué hora vino el alumno además del total.
- ✅ Mostrar última asistencia con fecha y hora al alumno en StudentDetailScreen
- ✅ El admin puede visualizar la última vez que ha asistido el alumno en StudentHomeScreen
- ✅ Asistencia.kt creada para llevar un registro de la asistencia de alumnos
- ✅ AdminViewModel y StudentViewModel updateados para implementar la asistencia
- ✅ Historial de las 10 últimas asistencias creado
- ✅ Función para formatear el timestamp añadida
- ✅ Scroll interno añadido a las asistencias

### [Alberto] - Fecha: 23/02/2026___
- ✅ Refactorización de Navegación: Consolidado AppRoot y AppRoot2 en un único archivo AppRoot.kt para eliminar código duplicado y evitar conflictos.
- ✅ Centralización de Rutas: Creado objeto Routes en AppRoot.kt para gestionar los nombres de las rutas de forma segura.
- ✅ Identidad Visual Corporativa (Theming):
- ✅ Definida paleta de colores "Black & White" en theme/Color.kt para un estilo profesional de artes marciales.
- ✅ Configurado theme/Theme.kt para usar los nuevos colores en modo claro y oscuro, y desactivado el color dinámico de Android para mantener la identidad de la marca.
- ✅ Rediseño Completo de la Interfaz de Usuario:
- ✅ Login y Registro: Añadido icono temporal, mejorada la tipografía y aplicados bordes redondeados para un look moderno.
- ✅ Perfil del Alumno: Rediseñada la StudentHomeScreen con tarjetas visuales para el cinturón y las asistencias, mejorando la jerarquía de la información.
- ✅ Panel de Admin: Rediseñada la AdminHomeScreen con tarjetas de alumno más limpias y una cabecera con el total de alumnos.
- ✅ Detalle y Añadir Alumno: Unificado el diseño de StudentDetailScreen y AddStudentScreen con el resto de la app.
- ✅Solución de Errores de Compilación:
- ✅ Eliminada por completo la librería de QR (alexzh:qrcode-compose) que causaba errores de Gradle y bloqueaba la ejecución de la app.
- ✅ Limpiados los archivos build.gradle.kts y StudentDetailScreen.kt de cualquier referencia a la librería eliminada para asegurar que el proyecto compile correctamente.
---

## Sesión extra - Semana 3 (25/02/2026)

### [Arturo] - Fecha: 25/02/2026 (con Claude Code como asistente)

#### Corrección de bugs críticos
- ✅ **Bug de IDs en Firestore corregido**: Los documentos se creaban con UID (SignUpScreen) o con email (AdminViewModel) como ID. Todas las operaciones de escritura fallaban silenciosamente porque intentaban acceder al documento por email, aunque algunos tenían UID como ID.
  - `obtenerAlumnos()`: ahora hace `copy(id = doc.id)` para capturar el ID real del documento
  - `actualizarAlumno()`: usa `document(alumnoFinal.id)` en vez de `document(user.email)`
  - `registrarAsistencia()`: recibe el ID real del documento, no el email
  - `StudentDetailScreen`: pasa `alumno.id` (ID real) en vez de `alumno.email`
- ✅ Añadidos logs de diagnóstico en AdminViewModel (Logcat tag: "AdminViewModel")

#### Nuevas funcionalidades (requisito del profesor José Manuel)

- ✅ **R04 - Activos vs Inactivos**: Detección automática de alumnos inactivos
  - Añadido campo `ultimaAsistencia: Long` al modelo `User.kt`
  - `registrarAsistencia()` actualiza `ultimaAsistencia` en cada registro
  - `calcularInactivos()` en AdminViewModel: marca como inactivo a quien lleve +30 días sin venir
  - Nueva pantalla `InactivosScreen.kt`: lista de inactivos con días sin venir
  - Tarjeta de acceso rápido en AdminHomeScreen con badge de contador rojo

- ✅ **R05 - Estadísticas básicas**: Pantalla de estadísticas generales
  - Nueva pantalla `EstadisticasScreen.kt` con 3 secciones:
    · Resumen: total de alumnos, activos vs inactivos (con porcentaje)
    · Distribución por cinturón con barras de progreso visuales (colores reales BJJ)
    · Top 5 alumnos con más asistencias
  - Tarjeta de acceso rápido en AdminHomeScreen

- ✅ **R06 - Buscador de alumnos**: Filtro en tiempo real en AdminHomeScreen
  - Campo de búsqueda encima de la lista
  - Filtra por nombre, ignorando mayúsculas/minúsculas
  - Muestra contador de resultados cuando hay búsqueda activa

- ✅ **R07 - Mejora de StudentHomeScreen**: Perfil del alumno enriquecido
  - Barra visual con el color real del cinturón (igual que en StudentDetailScreen)
  - Stripes/grados visibles en la barra del cinturón
  - Fecha desde cuándo tiene ese cinturón (en formato DD/MM/YYYY)
  - Estado ACTIVO/INACTIVO con días desde la última asistencia

#### Candidatos a Graduación (implementado anteriormente, documentado ahora)
- ✅ **R03 - Candidatos a Graduación**: Implementado por Arturo (25/02)
  - Nueva pantalla `CandidatosScreen.kt`
  - Reglas IBJJF simplificadas en `AdminViewModel.kt`:
    · Blanco → Azul: 6 meses + 80 clases
    · Azul → Morado: 24 meses + 200 clases
    · Morado → Marrón: 18 meses + 180 clases
    · Marrón → Negro: 12 meses + 150 clases
  - Añadido campo `fechaInicioCinturon: Long` al modelo `User.kt`
  - Se resetea automáticamente al guardar cambio de cinturón
  - Tarjeta de acceso rápido en AdminHomeScreen

#### Navegación
- ✅ `AppRoot.kt` unificado (legacy `AppRoot2.kt` eliminado)
- ✅ Objeto `Routes` con todas las rutas centralizadas
- ✅ Nuevas rutas: `candidatos`, `inactivos`, `estadisticas`
- ✅ `AdminHomeScreen` actualizado con los 3 nuevos parámetros de navegación

---

## Sesión extra – Semana 4 (25/02/2026) – Limpieza y mejoras

### [Arturo] - Fecha: 25/02/2026 (con Claude Code como asistente)

#### Limpieza general de código (todos los .kt)
- ✅ **AuthViewModel.kt**: Añadido `addOnFailureListener` en `checkUserRole()` — ya no falla silenciosamente si Firestore da error
- ✅ **StudentViewModel.kt**: Añadido `Log.e` en el handler de error de `loadCurrentStudentData()` — los errores de Firestore ahora aparecen en Logcat tag `StudentViewModel`
- ✅ **AdminHomeScreen.kt**: Añadido `import androidx.compose.ui.graphics.Color`; cambiado `androidx.compose.ui.graphics.Color?` por `Color?` en la firma de `TarjetaAccesoRapido`
- ✅ **StudentHomeScreen.kt**: Eliminado parámetro `email: String` que se recibía pero nunca se usaba (el ViewModel obtiene el usuario internamente via FirebaseAuth)
- ✅ **AppRoot.kt**: Eliminado import `AdminViewModel` (ya no se usa ahí). Simplificada la ruta de `AddStudent`: ya no crea un ViewModel local ni gestiona la lógica de registro (esa responsabilidad pasó a AddStudentScreen). Eliminado el `println("Error: $it")` residual.

#### Validaciones en AddStudentScreen
- ✅ **AddStudentScreen.kt** — reescritura completa:
  - La pantalla gestiona su propio `AdminViewModel` (patrón del resto de pantallas)
  - Validación de nombre: mínimo 2 caracteres (error debajo del campo)
  - Validación de email: debe contener `@` y `.` tras el `@` (error debajo del campo)
  - Validación de duplicado en Firestore: consulta por campo `email` (`whereEqualTo`) antes de registrar, sin importar si el documento existente usa UID o email como ID
  - Estado `loading` que deshabilita el botón y muestra "REGISTRANDO..." mientras se procesa
  - Error general para fallos de red/conexión
  - Estilo consistente: TopAppBar con "DAR DE ALTA ALUMNO" en bold, botón con `shape = MaterialTheme.shapes.medium`, texto uppercase

#### Manejo de errores en Firebase (callbacks vacíos corregidos)
- ✅ **StudentDetailScreen.kt**: Los callbacks vacíos `{}` de `registrarAsistencia` y `actualizarAlumno` ahora muestran un Snackbar con el mensaje de error. Añadido `SnackbarHost` al `Scaffold` y `rememberCoroutineScope`.

#### Advertencia sobre código legacy
- ⚠️ **HomeScreen.kt**: Este archivo existe pero NO se usa en el grafo de navegación (`AppRoot.kt`). Es código legacy de la semana 1 (Alejandra 09/02). No se ha eliminado — se informa al equipo para que decidan si borrarlo en una sesión de limpieza.

## Semana 4 - Registro de asistencia

### [Arturo] - Fecha: ___
-

### [Alejandra] - Fecha: ___
-

### [Alberto] - Fecha: ___
-

---

## Semana 5 - Testing y bugs

### [Arturo] - Fecha: ___
-

### [Alejandra] - Fecha: ___
-

### [Alberto] - Fecha: ___
-

---

## Semana 6 - Documentación y presentación

### [Arturo] - Fecha: ___
-

### [Alejandra] - Fecha: ___
-

### [Alberto] - Fecha: ___
-

---

## Cómo usar este archivo

1. Cada vez que hagas algo, añade una línea con lo que hiciste
2. Pon tu nombre y la fecha
3. Usa ✅ para tareas completadas
4. Usa ⏳ para tareas en progreso
5. Haz commit de este archivo junto con tus cambios
```

---

**Después de pegarlo, haz otro commit:**
```
git add .
```
```
git commit -m "Docs: Actualizado CHANGELOG con los cambios de la sesion del 14/02 - Arturo"
```

Y si quieres subirlo a GitHub:
```
git push
