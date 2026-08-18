-keep class androidx.room.** { *; }
-keep @androidx.room.Entity class * { *; }
-keepclassmembers class * {
    @androidx.room.PrimaryKey <fields>;
}
-keep class kotlin.** { *; }
-keep class kotlinx.coroutines.** { *; }
-keep class com.example.** { *; }