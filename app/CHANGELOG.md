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

### [Alejandra] - Fecha: ___
- 

### [Alberto] - Fecha: 23/02/2026___
- 
  ✅ Refactorización de Navegación: Consolidado AppRoot y AppRoot2 en un único archivo AppRoot.kt para eliminar código duplicado y evitar conflictos.
  ✅ Centralización de Rutas: Creado objeto Routes en AppRoot.kt para gestionar los nombres de las rutas de forma segura.
  ✅ Identidad Visual Corporativa (Theming):
  ✅ Definida paleta de colores "Black & White" en theme/Color.kt para un estilo profesional de artes marciales.
  ✅ Configurado theme/Theme.kt para usar los nuevos colores en modo claro y oscuro, y desactivado el color dinámico de Android para mantener la identidad de la marca.
  ✅ Rediseño Completo de la Interfaz de Usuario:
  ✅ Login y Registro: Añadido icono temporal, mejorada la tipografía y aplicados bordes redondeados para un look moderno.
  ✅ Perfil del Alumno: Rediseñada la StudentHomeScreen con tarjetas visuales para el cinturón y las asistencias, mejorando la jerarquía de la información.
  ✅ Panel de Admin: Rediseñada la AdminHomeScreen con tarjetas de alumno más limpias y una cabecera con el total de alumnos.
  ✅ Detalle y Añadir Alumno: Unificado el diseño de StudentDetailScreen y AddStudentScreen con el resto de la app.
  ✅ Solución de Errores de Compilación:
  ✅ Eliminada por completo la librería de QR (alexzh:qrcode-compose) que causaba errores de Gradle y bloqueaba la ejecución de la app.
  ✅ Limpiados los archivos build.gradle.kts y StudentDetailScreen.kt de cualquier referencia a la librería eliminada para asegurar que el proyecto compile correctamente.

---

## Semana 3 - Perfil del alumno

### [Arturo] - Fecha: ___
- 

### [Alejandra] - Fecha: ___
- 

### [Alberto] - Fecha: ___
- 

---

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