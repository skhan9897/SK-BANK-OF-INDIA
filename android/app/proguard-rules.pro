# SK Bank of India Official App ProGuard & Release Signing Rules

-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod, InnerClasses

# Retrofit 2
-keepclassmembers class * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**

# Gson & Jackson Models
-keep class com.google.gson.** { *; }
-keep class com.bank.skbankofindia.model.** { *; }

# OkHttp3
-dontwarn okhttp3.**
-dontwarn okio.**

# Room Database
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.**
