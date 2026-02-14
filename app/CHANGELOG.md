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
- ⚠️ PENDIENTE: Alberto debe documentar sus cambios aquí. Sus commits incluyen:
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

### [Alejandra] - Fecha: ___
- 

### [Alberto] - Fecha: ___
- 

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