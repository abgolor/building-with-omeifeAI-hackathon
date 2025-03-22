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

package com.ajaytechsolutions.ekotranslate.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.compose.rememberNavController
import com.ajaytechsolutions.ekotranslate.R
import com.ajaytechsolutions.ekotranslate.navigation.AppRoute
import com.ajaytechsolutions.ekotranslate.ui.theme.EkoTranslateTheme
import kotlinx.coroutines.delay

/**
 * SplashScreen composable that displays the app logo with animations.
 *
 * This screen is shown when the app is launched, featuring a scale and fade-in animation
 * for the app logo, followed by automatic navigation to the main screen after a delay.
 *
 * @param navController Navigation controller used to navigate to the main screen
 * @param splashDuration Duration in milliseconds to display the splash screen (default: 2000ms)
 * @param initialDelay Delay in milliseconds before starting animations (default: 300ms)
 * @param animationDuration Duration in milliseconds for the animations (default: 1000ms)
 */
@Composable
fun SplashScreen(
    navController: NavController,
    splashDuration: Long = 2000,
    initialDelay: Long = 300,
    animationDuration: Int = 1000,
) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val tintColor = MaterialTheme.colorScheme.onBackground

    var isLogoVisible by remember { mutableStateOf(false) }
    val scale = remember { Animatable(0.8f) }
    val alpha = remember { Animatable(0f) }

    // Start animation when the screen appears
    LaunchedEffect(Unit) {
        delay(initialDelay)
        isLogoVisible = true
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(animationDuration)
        )
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(animationDuration)
        )

        delay(splashDuration)
        navigateToMainScreen(navController)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        AppLogo(
            isVisible = isLogoVisible,
            scale = scale.value,
            alpha = alpha.value,
            tintColor = tintColor
        )

        PoweredByFooter(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp),
            tintColor = tintColor
        )
    }
}

/**
 * Animated app logo composable.
 *
 * @param isVisible Controls visibility of the logo
 * @param scale Scale factor for animation
 * @param alpha Alpha value for fade-in animation
 * @param tintColor Color to tint the logo
 */
@Composable
private fun AppLogo(
    isVisible: Boolean,
    scale: Float,
    alpha: Float,
    tintColor: androidx.compose.ui.graphics.Color,
) {
    AnimatedVisibility(visible = isVisible) {
        Image(
            painter = painterResource(id = R.drawable.eko_translate_logo),
            contentDescription = stringResource(R.string.app_logo_content_description),
            modifier = Modifier
                .width(100.dp)
                .scale(scale)
                .alpha(alpha),
            colorFilter = ColorFilter.tint(tintColor)
        )
    }
}

/**
 * Footer composable displaying "Powered by Omeife AI" text and logo.
 *
 * @param modifier Modifier to be applied to the footer container
 * @param tintColor Color to tint the text and logo
 */
@Composable
private fun PoweredByFooter(
    modifier: Modifier = Modifier,
    tintColor: androidx.compose.ui.graphics.Color,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.powered_by),
                color = tintColor,
                style = MaterialTheme.typography.labelSmall
            )

            Spacer(modifier = Modifier.width(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.omeife_logo),
                    contentDescription = stringResource(R.string.omeife_logo_content_description),
                    modifier = Modifier.width(30.dp),
                    colorFilter = ColorFilter.tint(tintColor)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = stringResource(R.string.omeife_ai),
                    color = tintColor,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

/**
 * Navigates from the splash screen to the main screen.
 *
 * @param navController Navigation controller to handle the navigation
 */
private fun navigateToMainScreen(navController: NavController) {
    val navOptions = NavOptions.Builder()
        .setPopUpTo(AppRoute.SPLASH.route, inclusive = true)
        .build()

    navController.navigate(AppRoute.MAIN.route, navOptions)
}

/**
 * Preview of the SplashScreen composable.
 */
@Preview(showBackground = true)
@Composable
private fun SplashScreenPreview() {
    EkoTranslateTheme {
        // Use rememberNavController() which is designed to be used in Composables
        val previewNavController = rememberNavController()
        SplashScreen(navController = previewNavController)
    }
}