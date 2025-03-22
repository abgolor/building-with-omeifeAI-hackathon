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

import androidx.compose.ui.graphics.Color

/**
 * Color palette for the EkoTranslate application.
 *
 * This object provides centralized access to all colors used throughout the app,
 * organized by theme (light/dark) and purpose.
 */
object EkoColors {
    /**
     * Light theme color palette
     */
    object Light {
        val Background = Color(0xFFFFFFFF)
        val Text = Color(0xFF000000)
        val SentBubble = Color(0xFF007AFF)
        val ReceivedBubble = Color(0xFFE5E5EA)
        val InputBackground = Color(0xFFF1F1F3)
    }

    /**
     * Dark theme color palette
     */
    object Dark {
        val Background = Color(0xFF000000)
        val Text = Color(0xFFFFFFFF)
        val SentBubble = Color(0xFF007AFF) // Keeping consistent with light theme
        val ReceivedBubble = Color(0xFF2C2C2E)
        val InputBackground = Color(0xFF3A3A3C)
    }

    /**
     * Common UI element colors used across both themes
     */
    object Common {
        val Black = Color(0xFF000000)
        val White = Color(0xFFFFFFFF)
        val Error = Color(0xFFFF3B30)
        val Success = Color(0xFF34C759)
        val Warning = Color(0xFFFF9500)
    }
}