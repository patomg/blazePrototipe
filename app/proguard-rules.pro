# Retrofit / OkHttp / Gson keep rules for release builds.
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.blaze.fitness.data.remote.dto.** { *; }
-dontwarn okhttp3.**
-dontwarn retrofit2.**
