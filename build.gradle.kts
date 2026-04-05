import java.util.Properties

val mapkitApiKey: String = run {
    val secretFile = file("secret.properties")
    if (!secretFile.exists()) return@run ""
    secretFile.inputStream().use { stream ->
        Properties().apply { load(stream) }.getProperty("MAPKIT_API_KEY", "")
    }
}
extra["mapkitApiKey"] = mapkitApiKey

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.android.library) apply false
}