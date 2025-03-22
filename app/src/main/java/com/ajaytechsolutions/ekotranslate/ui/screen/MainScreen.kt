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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ajaytechsolutions.ekotranslate.R
import com.ajaytechsolutions.ekotranslate.ui.components.AppBar
import com.ajaytechsolutions.ekotranslate.ui.theme.EkoTranslateTheme
import com.ajaytechsolutions.ekotranslate.utils.NetworkObserver
import com.ajaytechsolutions.ekotranslate.viewmodel.TranslationViewModel

/**
 * Main screen of the EkoTranslate application.
 *
 * This composable serves as the container for the app's main UI, integrating the app bar
 * and the chat interface. It handles system insets and network state observation.
 *
 * @param networkObserver Observer for monitoring network connectivity status
 * @param viewModel ViewModel that manages translation state and operations
 */
@Composable
fun MainScreen(
    networkObserver: NetworkObserver,
    viewModel: TranslationViewModel,
) {
    Surface(
        modifier = Modifier
            .safeDrawingPadding()
            .windowInsetsPadding(WindowInsets.ime),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            topBar = {
                TopBarSection(networkObserver = networkObserver)
            }
        ) { innerPadding ->
            ChatScreen(
                modifier = Modifier.padding(innerPadding),
                viewModel = viewModel
            )
        }
    }
}

/**
 * Top bar section containing the app bar and a divider.
 *
 * Extracted as a separate composable to improve readability and maintainability.
 *
 * @param networkObserver Observer for monitoring network connectivity status
 */
@Composable
private fun TopBarSection(networkObserver: NetworkObserver) {
    Column {
        AppBar(
            title = stringResource(R.string.app_name),
            networkObserver = networkObserver
        )
        HorizontalDivider()
    }
}

