package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
  primary = NestifyPrimary,
  onPrimary = NestifyOnPrimary,
  primaryContainer = NestifyPrimaryContainer,
  onPrimaryContainer = NestifyOnPrimaryContainer,
  secondary = NestifySecondary,
  onSecondary = NestifyOnSecondary,
  secondaryContainer = NestifySecondaryContainer,
  onSecondaryContainer = NestifyOnSecondaryContainer,
  tertiary = NestifyTertiary,
  onTertiary = NestifyOnTertiary,
  background = NestifyBackground,
  onBackground = NestifyOnBackground,
  surface = NestifySurface,
  onSurface = NestifyOnSurface,
  surfaceVariant = NestifySurfaceVariant,
  onSurfaceVariant = NestifyOnSurfaceVariant,
  outline = NestifyOutline,
  outlineVariant = NestifyOutlineVariant,
  error = NestifyError,
  onError = NestifyOnError,
  errorContainer = NestifyErrorContainer,
)

private val DarkColorScheme = darkColorScheme(
  primary = Color(0xFFA5A5FF),
  onPrimary = Color(0xFF1B1B6D),
  primaryContainer = Color(0xFF4343B5),
  onPrimaryContainer = Color(0xFFECECFC),
  secondary = Color(0xFFB4B0FF),
  onSecondary = Color(0xFF231E75),
  secondaryContainer = Color(0xFF5048D9),
  onSecondaryContainer = Color(0xFFF0EFFF),
  tertiary = NestifyTertiary,
  background = Color(0xFF12131A),
  onBackground = Color(0xFFF1F1F8),
  surface = Color(0xFF1A1B24),
  onSurface = Color(0xFFF1F1F8),
  surfaceVariant = Color(0xFF262836),
  onSurfaceVariant = Color(0xFFA0A4B8),
  outline = Color(0xFF3B3E52),
  outlineVariant = Color(0xFF2D3040),
  error = Color(0xFFFFB4AB),
  onError = Color(0xFF690005),
)

@Composable
fun NestifyTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
