# Guía de Desarrollo

## Estructura del Código

### Data Layer
- **AppRepository**: Gestiona toda la lógica de negocio y acceso a datos
- **AppDatabase**: Configuración de Room Database
- **DAOs**: Operaciones CRUD en la base de datos
- **Entities**: Mapeo de objetos de base de datos

### Models
- **User**: Representa un usuario del sistema
- **Lot**: Representa un lote de riego
- **Pulse**: Representa un pulso de riego
- **Thresholds**: Umbrales de drenaje
- **AppState**: Estado global de la aplicación

### UI Layer
- **MainActivity**: Punto de entrada de la aplicación
- **LoginScreen**: Pantalla de autenticación
- **DashboardScreen**: Panel de control principal

## Flujo de Datos

```
UI (Composables)
    ↓
AppRepository
    ↓
AppDatabase
    ↓
Room DAOs
    ↓
Android SQLite
```

## State Management

La aplicación utiliza StateFlow para reactividad:

```kotlin
val state: StateFlow<AppState> = _state.asStateFlow()
```

## Autenticación

El flujo de autenticación:

1. Usuario ingresa credenciales
2. AppRepository valida contra la base de datos
3. Si es válido, se actualiza el estado con currentUser
4. UI se recompone automáticamente

## Agregar Nuevas Funcionalidades

### 1. Crear un nuevo modelo
```kotlin
data class MyModel(
    val id: String,
    val value: String
)
```

### 2. Crear entity y DAO
```kotlin
@Entity(tableName = "my_table")
data class MyEntity(...)

@Dao
interface MyDao {
    @Query("SELECT * FROM my_table")
    fun getAll(): Flow<List<MyEntity>>
}
```

### 3. Agregar al Repository
```kotlin
fun myFunction() { ... }
```

### 4. Crear UI Composable
```kotlin
@Composable
fun MyScreen() { ... }
```

## Testing

Estructura recomendada:
```
app/src/test/
└── java/com/example/pulsoapp/
    ├── data/
    └── ui/
```

## Build Variants

- **Debug**: Versión para desarrollo con logging completo
- **Release**: Versión optimizada para producción

## Performance Tips

1. Usa LazyColumn para listas largas
2. Implementa paginación en queries grandes
3. Evita recomposiciones innecesarias con remember
4. Usa @Composable con parámetros inmutables

## Debugging

### Logs
```kotlin
Log.d("TAG", "Message")
```

### Database Inspector
En Android Studio: View → Tool Windows → Database Inspector

## Próximos Pasos

- [ ] Agregar sincronización cloud
- [ ] Implementar gráficos de datos
- [ ] Agregar notificaciones en tiempo real
- [ ] Exportar datos a CSV
- [ ] Soporte para múltiples idiomas
