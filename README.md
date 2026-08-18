# Pulso de Riego App

Aplicación Android para monitoreo y gestión de sistemas de riego.

## Características

- Autenticación de usuarios
- Monitoreo de lotes y drenaje
- Sistema de pulsos de riego
- Base de datos local con Room
- UI moderna con Jetpack Compose

## Credenciales de Prueba

- Usuario: `admin` | Contraseña: `admin`
- Usuario: `ing_riego` | Contraseña: `1234`
- Usuario: `regador` | Contraseña: `1234`

## Requisitos

- Android 24+
- Android Studio 2023+

## Instalación

```bash
cd Pulso-riego-app
./gradlew build
```

## Estructura del Proyecto

```
app/
├── src/main/
│   ├── java/com/example/pulsoapp/
│   │   ├── MainActivity.kt
│   │   ├── data/
│   │   │   ├── AppRepository.kt
│   │   │   ├── models/
│   │   │   └── database/
│   │   └── ui/
│   └── res/
└── build.gradle.kts
```

## Estado de Drenaje

- **Óptimo**: 10% - 40%
- **Déficit**: < 10%
- **Exceso**: > 40%