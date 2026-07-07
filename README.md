# OSSA Control 🥋

Aplicación Android de gestión integral para academias de Brazilian Jiu-Jitsu: alumnos, asistencia, estadísticas y sistema de grados según normativa IBJJF.

Proyecto de fin de ciclo del FP de Desarrollo de Aplicaciones Multiplataforma (DAM), desarrollado en equipo. Nace de un problema real: la gestión diaria de una academia con más de 500 alumnos.

## Funcionalidades

- **Gestión de alumnos** — altas, bajas, fichas completas y búsqueda
- **Control de asistencia** — registro por clase y histórico por alumno
- **Sistema de grados IBJJF** — cinturones y grados con requisitos de tiempo según normativa oficial
- **Estadísticas** — asistencia, progresión y actividad de la academia
- **Autenticación** — acceso seguro con Firebase Auth
- **Sincronización en la nube** — datos en tiempo real con Cloud Firestore

8 funcionalidades desarrolladas y probadas.

## Stack técnico

| Capa | Tecnología |
|---|---|
| Lenguaje | Kotlin |
| UI | Jetpack Compose |
| Arquitectura | MVVM |
| Backend | Firebase (Auth + Cloud Firestore) |
| Reglas de seguridad | Firestore Security Rules |
| Control de versiones | Git |

## Arquitectura

Patrón **MVVM** con separación de responsabilidades:

```
app/
 ├── ui/          → Pantallas y componentes (Jetpack Compose)
 ├── viewmodel/   → Lógica de presentación y estado
 ├── data/        → Repositorios y acceso a Firestore
 └── model/       → Modelos de dominio
```

## Cómo ejecutarlo

1. Clonar el repositorio
   ```bash
   git clone https://github.com/arturosalasespies/OssaControl.git
   ```
2. Abrir el proyecto en Android Studio
3. Crear un proyecto en [Firebase Console](https://console.firebase.google.com/) y descargar el `google-services.json` propio en `app/`
4. Activar **Authentication** (email/contraseña) y **Cloud Firestore** en Firebase
5. Ejecutar en emulador o dispositivo (API 26+)

## Documentación

Documentación adicional del proyecto en la carpeta [`/docs`](./docs).

## Autor

**Arturo Salas Espies** — [LinkedIn](https://www.linkedin.com/in/arturosalas-ia) · Desarrollado en equipo como proyecto final del FP DAM.
