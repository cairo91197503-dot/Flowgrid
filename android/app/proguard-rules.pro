# Hilt
-keep @dagger.hilt.android.AndroidEntryPoint class *
-keep @dagger.hilt.android.HiltAndroidApp class *

# Room
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao interface *

# Billing
-keep class com.android.billingclient.** { *; }

# AdMob
-keep class com.google.android.gms.ads.** { *; }

# General Kotlin / Coroutines
-keep class kotlinx.coroutines.** { *; }
-keep class kotlinx.serialization.** { *; }
