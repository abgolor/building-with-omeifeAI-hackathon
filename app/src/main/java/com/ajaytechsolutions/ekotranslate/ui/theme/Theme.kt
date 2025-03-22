/**
 * MIT License
 *
 * Copyright (c) 2025 Eko Translate
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.ajaytechsolutions.ekotranslate.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController

/**
 * Color definitions for EkoTranslate app.
 * These colors are organized by theme mode (light/dark) and purpose.
 */

/* Light Theme Colors */
private val LightBackground = Color(0xFFFFFFFF)
private val LightText = Color(0xFF000000)
internal val LightSentBubble = Color(0xFF007AFF)
private val LightReceivedBubble = Color(0xFFE5E5EA)
private val LightInputBackground = Color(0xFFF1F1F3)

/* Dark Theme Colors */
private val DarkBackground = Color(0xFF000000)
private val DarkText = Color(0xFFFFFFFF)
private val DarkSentBubble = Color(0xFF007AFF) // Keeping consistent with light theme
private val DarkReceivedBubble = Color(0xFF2C2C2E)
private val DarkInputBackground = Color(0xFF3A3A3C)

/* Common UI Element Colors */
private val PrimaryBlack = Color(0xFF000000)
private val SecondaryWhite = Color(0xFFFFFFFF)

/**
 * Light color scheme for the EkoTranslate application.
 * Defines color mappings for Material3 components in light mode.
 */
private val LightColorScheme = lightColorScheme(
    background = LightBackground,
    onBackground = LightText,
    primary = PrimaryBlack,
    surface = LightReceivedBubble,
    onPrimary = SecondaryWhite,
    onSurface = LightText,
    secondaryContainer = LightInputBackground,
    onSecondaryContainer = LightText,
    primaryContainer = LightSentBubble,
    onPrimaryContainer = SecondaryWhite
)

/**
 * Dark color scheme for the EkoTranslate application.
 * Defines color mappings for Material3 components in dark mode.
 */
private val DarkColorScheme = darkColorScheme(
    background = DarkBackground,
    onBackground = DarkText,
    primary = SecondaryWhite,
    surface = DarkReceivedBubble,
    onPrimary = PrimaryBlack,
    onSurface = DarkText,
    secondaryContainer = DarkInputBackground,
    onSecondaryContainer = DarkText,
    primaryContainer = DarkSentBubble,
    onPrimaryContainer = SecondaryWhite
)

/**
 * Main theme for the EkoTranslate application.
 *
 * @param darkTheme Whether to use dark theme, defaults to system setting
 * @param dynamicColor Whether to use dynamic colors (Android 12+)
 * @param content The content to be themed
 */
@Composable
fun EkoTranslateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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

    val systemUiController = rememberSystemUiController()

    SideEffect {
        // Update system bars to match app theme
        systemUiController.setSystemBarsColor(
            color = colorScheme.background
        )
        systemUiController.setNavigationBarColor(
            color = colorScheme.background
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

/**
 * Preview of the EkoTranslate light theme.
 */
@Preview(name = "Light Theme", showBackground = true)
@Composable
fun LightThemePreview() {
    EkoTranslateTheme(darkTheme = false) {
        ThemePreviewContent()
    }
}

/**
 * Preview of the EkoTranslate dark theme.
 */
@Preview(name = "Dark Theme", showBackground = true)
@Composable
fun DarkThemePreview() {
    EkoTranslateTheme(darkTheme = true) {
        ThemePreviewContent()
    }
}

/**
 * Shared content for theme previews.
 */
@Composable
private fun ThemePreviewContent() {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "EkoTranslate",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "A messaging app with translation capabilities",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}