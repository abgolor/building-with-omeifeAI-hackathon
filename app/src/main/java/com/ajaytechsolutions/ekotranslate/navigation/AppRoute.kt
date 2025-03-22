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

package com.ajaytechsolutions.ekotranslate.navigation

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ajaytechsolutions.ekotranslate.ui.screen.MainScreen
import com.ajaytechsolutions.ekotranslate.ui.screen.SplashScreen
import com.ajaytechsolutions.ekotranslate.ui.theme.EkoTranslateTheme
import com.ajaytechsolutions.ekotranslate.utils.NetworkObserver
import com.ajaytechsolutions.ekotranslate.viewmodel.TranslationViewModel

/**
 * Enum class defining all navigation routes in the application.
 * This ensures type safety when navigating between screens.
 */
enum class AppRoute(val route: String) {
    SPLASH("splash_screen"),
    MAIN("main_screen")
}

/**
 * Main navigation component for the EkoTranslate application.
 * Sets up the navigation graph and handles routing between screens.
 *
 * @param networkObserver Observer for network connectivity status
 * @param translationViewModel ViewModel providing translation functionality
 * @param navController Optional NavHostController; if not provided, one will be created
 */
@Composable
fun AppNavigation(
    networkObserver: NetworkObserver,
    translationViewModel: TranslationViewModel,
    navController: NavHostController = rememberNavController()
) {
    Surface(color = MaterialTheme.colorScheme.background) {
        NavHost(
            navController = navController,
            startDestination = AppRoute.SPLASH.route
        ) {
            composable(AppRoute.SPLASH.route) {
                SplashScreen(navController = navController)
            }
            
            composable(AppRoute.MAIN.route) {
                MainScreen(
                    networkObserver = networkObserver,
                    viewModel = translationViewModel
                )
            }
        }
    }
}

/**
 * Preview for AppNavigation in light theme.
 * Uses mock objects for preview purposes.
 */
@Preview(showBackground = true, name = "Light Mode")
@Composable
private fun AppNavigationPreviewLight() {
    EkoTranslateTheme(darkTheme = false) {
        // Preview would use mock objects in a real implementation
    }
}

/**
 * Preview for AppNavigation in dark theme.
 * Uses mock objects for preview purposes.
 */
@Preview(showBackground = true, name = "Dark Mode")
@Composable
private fun AppNavigationPreviewDark() {
    EkoTranslateTheme(darkTheme = true) {
        // Preview would use mock objects in a real implementation
    }
}