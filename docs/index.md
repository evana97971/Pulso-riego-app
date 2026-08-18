# Pulso de Riego

**Aplicación Android para monitoreo y gestión de sistemas de riego**

## 🌾 Características

- ✅ Autenticación de usuarios con múltiples roles
- ✅ Monitoreo de lotes y drenaje en tiempo real
- ✅ Sistema de pulsos de riego
- ✅ Base de datos local con Room Database
- ✅ UI moderna con Jetpack Compose
- ✅ Análisis de estado de drenaje (Óptimo, Déficit, Exceso)

## 🔐 Credenciales de Prueba

| Usuario | Contraseña | Rol |
|---------|-----------|-----|
| `admin` | `admin` | Administrador |
| `ing_riego` | `1234` | Ingeniero |
| `regador` | `1234` | Operador |

## 📋 Requisitos

- Android 24+
- Android Studio 2023+
- Gradle 8.0+

## 🚀 Instalación

### Clonar el repositorio
```bash
git clone https://github.com/evana97971/Pulso-riego-app.git
cd Pulso-riego-app
```

### Compilar y ejecutar
```bash
# Sincronizar Gradle
./gradlew sync

# Compilar
./gradlew build

# Ejecutar en emulador o dispositivo
./gradlew installDebug
```

## 📱 Uso de la Aplicación

### Pantalla de Login
1. Ingresa tu usuario o email
2. Ingresa tu contraseña
3. Haz clic en "Iniciar Sesión"

### Dashboard Principal
- Visualiza todos los lotes de riego
- Verifica el estado de drenaje de cada lote
- Monitorea el porcentaje de drenaje
- Consulta recomendaciones automáticas

## 📊 Estados de Drenaje

| Estado | Rango | Acción |
|--------|-------|--------|
| ✅ **ÓPTIMO** | 10% - 40% | Mantener programa actual |
| ⚠️ **DÉFICIT** | < 10% | Aumentar volumen o frecuencia |
| ⛔ **EXCESO** | > 40% | Reducir tiempo de riego |

## 🏗️ Arquitectura del Proyecto

```
app/src/main/
├── java/com/example/pulsoapp/
│   ├── MainActivity.kt              # Actividad principal
│   ├── data/
│   │   ├── AppRepository.kt         # Lógica de negocio
│   │   ├── models/
│   │   │   └── Models.kt            # Data classes
│   │   └── database/
│   │       ├── AppDatabase.kt       # Database
│   │       ├── Entities.kt          # Room entities
│   │       └── Daos.kt              # DAOs
│   └── ui/theme/
│       └── Theme.kt                 # Material Design 3
├── res/
│   ├── values/
│   │   ├── colors.xml               # Paleta de colores
│   │   ├── strings.xml              # Strings
│   │   └── styles.xml               # Estilos
│   └── xml/
│       ├── backup_rules.xml
│       └── data_extraction_rules.xml
└── AndroidManifest.xml

build.gradle.kts                      # Configuración del proyecto
settings.gradle.kts                   # Módulos del proyecto
gradle.properties                     # Propiedades de Gradle
```

## 🔧 Dependencias Principales

- **Jetpack Compose** - UI moderna
- **Room Database** - Persistencia local
- **Kotlin Coroutines** - Programación asíncrona
- **Material Design 3** - Componentes de UI

## 📝 Modelos de Datos

### User
```kotlin
data class User(
    val id: String,
    val username: String,
    val fullName: String,
    val email: String,
    val password: String,
    val role: String
)
```

### Lot
```kotlin
data class Lot(
    val id: String,
    val avgDrenaje: Double? = null
)
```

### Pulse
```kotlin
data class Pulse(
    val id: Int = 0,
    val lote: String,
    val sfr_ml: Double,
    val drenaje_pct: Double,
    val inicio: String,
    val fin: String
)
```

## 🎨 Personalización

### Cambiar colores
Edita `app/src/main/res/values/colors.xml`

### Cambiar umbrales de drenaje
Modifica en `data/AppRepository.kt`:
```kotlin
Thresholds(high = 40.0, low = 10.0)
```

## 🐛 Solución de Problemas

### La app no compila
1. Asegúrate de tener Gradle 8.0+
2. Ejecuta `./gradlew clean` y luego `./gradlew build`

### Error de base de datos
1. Limpia el almacenamiento de la app
2. Desinstala y reinstala

### No puedes iniciar sesión
1. Verifica que escribiste correctamente el usuario y contraseña
2. Recuerda que es sensible a mayúsculas

## 📄 Licencia

Este proyecto está disponible bajo licencia MIT.

## 👤 Autor

**Eva Quispe**
- GitHub: [@evana97971](https://github.com/evana97971)
- Email: evana97971@gmail.com

## 🤝 Contribuciones

Las contribuciones son bienvenidas. Por favor:

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📞 Soporte

Si tienes preguntas o problemas, abre un issue en [GitHub Issues](https://github.com/evana97971/Pulso-riego-app/issues)

---

**Última actualización**: Agosto 2026
