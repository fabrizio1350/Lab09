package com.example.aplicacion.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// Ajustamos el ColorScheme para que use nuestra nueva paleta Marketplace
private val LightColorScheme = lightColorScheme(
    primary = PrimaryMarket,
    secondary = SecondaryMarket,
    tertiary = TertiaryMarket,
    background = BackgroundMarket,
    surface = SurfaceMarket,
    onPrimary = SurfaceMarket, // El texto sobre lo primario será blanco
    onBackground = SecondaryMarket,
    onSurface = SecondaryMarket
)

// Opcional: Podrías hacer un DarkTheme, pero por ahora lo forzamos a que use el Light
// para garantizar que siempre se vea como el diseño que queremos (blanco y limpio).
private val DarkColorScheme = LightColorScheme

@Composable
fun APLICACIONTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Desactivamos los colores dinámicos para que no tome los colores del fondo de pantalla del usuario
    dynamicColor: Boolean = false, 
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
