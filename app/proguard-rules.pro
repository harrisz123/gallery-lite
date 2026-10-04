# Coil 3 rules
-keep class io.coilkt.coil3.** { *; }
-dontwarn io.coilkt.coil3.**

# Room rules
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Media3 rules
-keep class androidx.media3.** { *; }

# Kotlinx coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
