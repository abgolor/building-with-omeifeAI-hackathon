/*
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

package com.ajaytechsolutions.ekotranslate.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * A typing indicator component that displays three animated bouncing dots.
 *
 * This component provides a smooth animation effect, simulating a user typing.
 * It follows best practices for Compose UI animations.
 */
@Composable
fun TypingIndicator() {
    val animValues = List(3) { remember { Animatable(1f) } }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            while (true) {
                animValues.forEachIndexed { index, animatable ->
                    launch {
                        animatable.animateTo(
                            targetValue = 1.4f,
                            animationSpec = keyframes {
                                durationMillis = 500
                                1.2f at 250 using LinearOutSlowInEasing // Bounce up
                                1f at 500 // Return to normal
                            }
                        )
                    }
                    delay(150) // Staggered animation effect
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .wrapContentSize(Alignment.CenterStart)
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp, 18.dp, 18.dp, 4.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            animValues.forEachIndexed { index, animatable ->
                Box(
                    modifier = Modifier
                        .padding(end = if (index < 2) 6.dp else 0.dp) // Space between dots
                        .size(6.dp * animatable.value) // Scaling for animation
                        .background(
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 1f),
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

/**
 * Preview of [TypingIndicator] to be displayed in Android Studio.
 */
@Composable
@Preview(showBackground = true)
fun TypingIndicatorPreview() {
    TypingIndicator()
}
