package com.poliba.pwdmanagerpolibaproject.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Blue500,
    onPrimary = White,
    primaryContainer = Blue700,
    onPrimaryContainer = White,
    secondary = Purple500,
    onSecondary = White,
    secondaryContainer = Purple700,
    onSecondaryContainer = White,
    tertiary = Blue300,
    onTertiary = Black,
    tertiaryContainer = Blue600,
    onTertiaryContainer = White,
    background = Gray900,
    onBackground = White,
    surface = Gray800,
    onSurface = White,
    surfaceVariant = Gray700,
    onSurfaceVariant = White,
    outline = Gray600,
    inverseOnSurface = Gray900,
    inverseSurface = Gray100,
    inversePrimary = Blue700,
    surfaceTint = Blue500,
    outlineVariant = Gray700,
    scrim = Black
)

private val LightColorScheme = lightColorScheme(
    primary = Blue500,
    onPrimary = White,
    primaryContainer = Blue100,
    onPrimaryContainer = Blue900,
    secondary = Purple500,
    onSecondary = White,
    secondaryContainer = Purple100,
    onSecondaryContainer = Purple900,
    tertiary = Blue300,
    onTertiary = Black,
    tertiaryContainer = Blue100,
    onTertiaryContainer = Blue900,
    background = Gray100,
    onBackground = Black,
    surface = White,
    onSurface = Black,
    surfaceVariant = Gray200,
    onSurfaceVariant = Black,
    outline = Gray400,
    inverseOnSurface = Gray100,
    inverseSurface = Gray900,
    inversePrimary = Blue100,
    surfaceTint = Blue500,
    outlineVariant = Gray300,
    scrim = Black
)

@Composable
fun PwdManagerPolibaProjectTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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