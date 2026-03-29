# Resumen Técnico — OSSA Control

**Proyecto**: OSSA Control
**Equipo**: Arturo (líder, lógica, testing, documentación) · Alberto (backend, Firebase, MVVM) · Alejandra (UI, pantallas)
**Última actualización**: 25/02/2026

---

## 1. Descripción General

OSSA Control es una app Android de gestión para la academia de Brazilian Jiu-Jitsu **OSSA BJJ**. Permite al coach (admin) gestionar su alumnado: registrar asistencias, controlar el progreso de cada alumno (cinturón y stripes), detectar alumnos inactivos y ver estadísticas generales. Los alumnos pueden ver su propio perfil con su cinturón, clases asistidas y estado de actividad.

---

## 2. Stack Tecnológico

| Componente | Tecnología |
|---|---|
| Lenguaje | Kotlin 2.0.21 |
| UI | Jetpack Compose (Material3) |
| Compose BOM | 2024.09.00 (Material3 1.3.0) |
| Arquitectura | MVVM |
| Auth | Firebase Authentication |
| Base de datos | Firebase Firestore (NoSQL, tiempo real) |
| Navegación | Navigation Compose 2.8.5 |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 34 (Android 14) |
| Build system | Gradle con Version Catalog |

---

## 3. Arquitectura MVVM

La app usa el patrón **Model-View-ViewModel**:

```
UI (Compose)  ←observa→  ViewModel  ←lee/escribe→  Firebase
   Screens                  States                  Firestore / Auth
```

- **View**: Las pantallas Composable (archivos `*Screen.kt`) solo muestran datos y llaman funciones del ViewModel. No contienen lógica de negocio.
- **ViewModel**: Contiene toda la lógica (calcular candidatos, detectar inactivos, CRUD de alumnos). Expone estados observables (`State<T>`) que la UI escucha.
- **Model**: La clase `User.kt` define la estructura de datos de un usuario/alumno.

### Flujo de datos
1. La pantalla llama `viewModel.obtenerAlumnos()`
2. El ViewModel registra un listener en Firestore (`addSnapshotListener`)
3. Cuando Firestore actualiza datos, el listener se dispara
4. El ViewModel actualiza su `_usuarios` (State observable)
5. Compose re-renderiza automáticamente la pantalla con los nuevos datos

---

## 4. Estructura de Carpetas

```
app/src/main/java/com/ossacontrol/app/
├── MainActivity.kt              — Punto de entrada de la app
├── model/
│   └── User.kt                  — Modelo de datos del usuario
├── ui/
│   ├── AppRoot.kt               — Mapa de navegación (NavHost + Routes)
│   ├── screens/
│   │   ├── LoginScreen.kt       — Pantalla de inicio de sesión
│   │   ├── SignUpScreen.kt      — Pantalla de registro de cuenta
│   │   ├── AdminHomeScreen.kt   — Panel principal del admin
│   │   ├── AddStudentScreen.kt  — Formulario alta de alumno
│   │   ├── StudentDetailScreen.kt — Detalle y edición de alumno
│   │   ├── CandidatosScreen.kt  — Alumnos candidatos a graduación
│   │   ├── InactivosScreen.kt   — Alumnos sin asistir en 30+ días
│   │   ├── EstadisticasScreen.kt — Estadísticas generales
│   │   ├── StudentHomeScreen.kt — Perfil del alumno
│   │   └── HomeScreen.kt        — Selector de rol (legacy)
│   └── theme/
│       ├── Color.kt             — Paleta de colores corporativa (Black & White)
│       ├── Theme.kt             — Configuración del tema Material3
│       └── Type.kt              — Tipografía
└── viewmodel/
    ├── AuthViewModel.kt         — Autenticación y roles
    ├── AdminViewModel.kt        — Gestión de alumnos, asistencia, candidatos, inactivos
    └── StudentViewModel.kt      — Perfil del alumno logueado
```

---

## 5. Pantallas

### Pantallas del Admin (rol: "admin")

| Pantalla | Archivo | Descripción |
|---|---|---|
| Panel Admin | `AdminHomeScreen.kt` | Lista de alumnos con buscador. Accesos rápidos a Candidatos, Inactivos y Estadísticas. FAB para añadir alumno. |
| Añadir Alumno | `AddStudentScreen.kt` | Formulario con nombre y email. El admin da de alta al alumno sin contraseña (solo el admin lo gestiona). |
| Detalle Alumno | `StudentDetailScreen.kt` | Vista completa del alumno: nombre, cinturón (barra visual con color real), selector de 5 cinturones BJJ, selector de stripes (0-4), contador de clases con botón +1. |
| Candidatos | `CandidatosScreen.kt` | Alumnos que cumplen los requisitos mínimos IBJJF (tiempo + clases) para subir de cinturón. |
| Inactivos | `InactivosScreen.kt` | Alumnos que llevan más de 30 días sin asistir. Muestra días sin venir o "NUNCA" si nunca han asistido. |
| Estadísticas | `EstadisticasScreen.kt` | 3 secciones: resumen (total/activos/inactivos), distribución por cinturón (barras visuales), top 5 asistencias. |

### Pantallas del Alumno (rol: "alumno")

| Pantalla | Archivo | Descripción |
|---|---|---|
| Perfil Alumno | `StudentHomeScreen.kt` | Nombre, barra visual del cinturón con stripes, fecha desde cuándo tiene ese cinturón, clases totales, estado ACTIVO/INACTIVO con días desde última asistencia. |

### Pantallas de Autenticación

| Pantalla | Archivo | Descripción |
|---|---|---|
| Login | `LoginScreen.kt` | Email y contraseña. Firebase Auth. |
| Registro | `SignUpScreen.kt` | Nombre, email y contraseña. Crea cuenta en Firebase Auth y documento en Firestore. |

---

## 6. ViewModels

### AuthViewModel.kt
Gestiona la autenticación y la detección de roles.

| Función | Descripción |
|---|---|
| `checkUserRole()` | Busca el documento del usuario logueado en Firestore y lee su campo `rol` |
| `logout()` | Cierra sesión en Firebase Auth y limpia el estado |

**Estado**: `userRole: State<String?>` — puede ser `"admin"`, `"alumno"` o `null`

### AdminViewModel.kt
Contiene toda la lógica del panel del administrador.

| Función | Descripción |
|---|---|
| `obtenerAlumnos()` | Listener en tiempo real de todos los alumnos. Cada documento se normaliza con `copy(id = doc.id)`. Llama automáticamente a `calcularCandidatos()` y `calcularInactivos()` |
| `registrarAlumno(nombre, email)` | Crea nuevo alumno en Firestore con email como ID del documento |
| `actualizarAlumno(user)` | Actualiza datos. Si cambió el cinturón, reinicia `fechaInicioCinturon` y `grados = 0` |
| `registrarAsistencia(alumnoId)` | Incrementa `clasesAsistidas` en +1 y actualiza `ultimaAsistencia` (ambos en una sola operación de Firestore) |
| `calcularCandidatos()` | Privada. Comprueba reglas IBJJF para cada alumno. Actualiza `_candidatos` |
| `calcularInactivos()` | Privada. Detecta alumnos con `ultimaAsistencia == 0L` o > 30 días sin venir. Actualiza `_inactivos` |

**Estados**:
- `usuarios: State<List<User>>` — todos los alumnos
- `candidatos: State<List<CandidatoInfo>>` — alumnos que pueden graduarse
- `inactivos: State<List<InactivoInfo>>` — alumnos inactivos

**Constante configurable**: `DIAS_INACTIVIDAD = 30`

### StudentViewModel.kt
Carga el perfil del alumno que tiene la sesión iniciada.

| Función | Descripción |
|---|---|
| `loadCurrentStudentData()` | Escucha en tiempo real el documento del usuario logueado (busca por UID) |

**Estado**: `studentData: State<User?>` — datos del alumno o null mientras carga

---

## 7. Modelo de Datos (User.kt)

```kotlin
data class User(
    val id: String = "",                  // ID real del documento en Firestore
                                          // Puede ser UID (SignUp) o email (Admin)
    val email: String = "",               // Email del alumno
    val nombre: String = "",              // Nombre completo
    val rol: String = "alumno",           // "admin" o "alumno"
    val cinturon: String = "Blanco",      // Blanco | Azul | Morado | Marrón | Negro
    val grados: Int = 0,                  // Stripes: 0-4
    val clasesAsistidas: Int = 0,         // Contador total de clases registradas
    val fechaInicioCinturon: Long = 0L,   // Milisegundos de cuándo se puso el cinturón actual
                                          // 0L = fecha no registrada (alumnos antiguos)
    val ultimaAsistencia: Long = 0L       // Milisegundos de la última asistencia registrada
                                          // 0L = nunca ha asistido o dato no registrado
)
```

**Importante**: Todos los campos tienen valor por defecto para que `toObject(User::class.java)` no falle si faltan campos en el documento de Firestore (compatibilidad con datos antiguos).

---

## 8. Estructura de Firestore

### Colección: `users/`

Cada documento representa un usuario (admin o alumno).

**Problema de IDs mixtos** (por diseño del equipo):
- Alumnos creados desde `SignUpScreen` → ID del documento = **UID de Firebase Auth**
- Alumnos creados desde `AddStudentScreen` (admin) → ID del documento = **email**

**Solución implementada**: `obtenerAlumnos()` siempre hace `.copy(id = doc.id)` para normalizar el ID real en el objeto User. Todas las escrituras usan `alumno.id` (nunca `alumno.email` como ID).

### Campos de cada documento:

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | String | Almacenado en el documento. Puede ser UID o email. |
| `email` | String | Email del usuario |
| `nombre` | String | Nombre completo |
| `rol` | String | `"admin"` o `"alumno"` |
| `cinturon` | String | Cinturón actual |
| `grados` | Number | Stripes (0-4) |
| `clasesAsistidas` | Number | Contador de clases |
| `fechaInicioCinturon` | Number | Timestamp (ms) del cinturón actual |
| `ultimaAsistencia` | Number | Timestamp (ms) de la última clase |

---

## 9. Reglas de Graduación IBJJF (simplificadas)

Implementadas en `AdminViewModel.REGLAS_GRADUACION`:

| Cinturón actual | Meses mínimos | Clases mínimas | Siguiente cinturón |
|---|---|---|---|
| Blanco | 6 meses | 80 clases | Azul |
| Azul | 24 meses (2 años) | 200 clases | Morado |
| Morado | 18 meses (1.5 años) | 180 clases | Marrón |
| Marrón | 12 meses (1 año) | 150 clases | Negro |

El cinturón Negro no tiene siguiente cinturón (no aparece en las reglas).

**Nota**: El profesor siempre tiene la última palabra. El sistema es una herramienta de apoyo, no una promoción automática.

---

## 10. Regla de Activos/Inactivos

- **Umbral**: `AdminViewModel.DIAS_INACTIVIDAD = 30` (días, configurable)
- **Inactivo si**: `ultimaAsistencia == 0L` (nunca asistió) o han pasado más de 30 días desde `ultimaAsistencia`
- **Cálculo**: se ejecuta automáticamente en cada actualización de Firestore
- `InactivoInfo.diasSinAsistir = -1` es el código especial para "nunca ha asistido"

---

## 11. Grafo de Navegación

```
                    ┌─────────────────────────────────────────────────────┐
                    │                    AppRoot.kt                       │
                    └─────────────────────────────────────────────────────┘
                                          │
                          ┌───────────────┴───────────────┐
                          ▼                               ▼
                       login                       home_selector
                     /       \                          │
                 signup     [login ok]           checkUserRole()
                                │                       │
                    ┌───────────┴──────────┐            │
                    ▼                      ▼            │
              admin_home            student_home ◄──────┘
            /    |    |    \
           /     |    |     \
     add_student |   candidatos
          student_detail/{email}
                  |
               inactivos
                  |
             estadisticas
```

### Objeto Routes (todas las rutas como constantes):
```kotlin
object Routes {
    const val Login = "login"
    const val SignUp = "signup"
    const val HomeSelector = "home_selector"
    const val AdminHome = "admin_home"
    const val StudentHome = "student_home"
    const val AddStudent = "add_student"
    const val StudentDetail = "student_detail"   // dinámica: student_detail/{email}
    const val Candidatos = "candidatos"
    const val Inactivos = "inactivos"
    const val Estadisticas = "estadisticas"
}
```

---

## 12. Dependencias del Proyecto

Definidas en `app/build.gradle.kts` y `gradle/libs.versions.toml`:

| Dependencia | Versión | Uso |
|---|---|---|
| Kotlin | 2.0.21 | Lenguaje |
| Jetpack Compose BOM | 2024.09.00 | UI framework |
| Navigation Compose | 2.8.5 | Navegación entre pantallas |
| Firebase BOM | 33.9.0 | Plataforma Firebase |
| Firebase Auth KTX | (BOM) | Autenticación de usuarios |
| Firebase Firestore KTX | (BOM) | Base de datos en tiempo real |
| Material Icons Extended | (BOM) | Iconos adicionales de Material Design |
| ZXing Core | 3.5.3 | Preparado para QR en el futuro (no activo aún) |

---

## 13. Indicaciones del Profesor José Manuel Implementadas

| Indicación | Implementación |
|---|---|
| "5 cinturones BJJ con colores reales" | `colorDelCinturon()` en StudentDetailScreen.kt |
| "Stripes/grados (0-4) por cinturón" | Selector FilterChip en StudentDetailScreen.kt |
| "Candidatos a graduación con reglas IBJJF" | CandidatosScreen.kt + AdminViewModel.calcularCandidatos() |
| "Inactivo = no ha asistido en 30 días (configurable)" | InactivosScreen.kt + AdminViewModel.DIAS_INACTIVIDAD |
| "Pantalla/filtro rápido para el coach" | InactivosScreen.kt con lista ordenada por días sin venir |
| "Estadísticas básicas: total activos, asistencias, top asistencia" | EstadisticasScreen.kt |
| "QR es para el futuro" | No implementado (se eliminó, queda pendiente) |

---

## 14. Pendiente para el Futuro

- **QR de asistencia**: Generar QR único por sesión y escanear para registrar asistencia automáticamente (el profesor dijo que es para más adelante)
- **Notificaciones push**: Avisar al coach cuando un alumno lleva X días sin venir
- **Sistema de cuotas/pagos**: Gestión de mensualidades
- **Reglas de seguridad en Firestore**: Actualmente no hay reglas de servidor; la lógica de roles está solo en la app
- **Mejora de checkUserRole()**: No funciona para alumnos creados por el admin que hagan login (busca por UID pero el documento tiene ID=email)

---

*Documento generado por Arturo  — 25/02/2026*
