/**
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

package com.ajaytechsolutions.ekotranslate.utils

import android.content.Context
import android.media.MediaPlayer
import com.ajaytechsolutions.ekotranslate.R

/**
 * Utility class for managing sounds in the EkoTranslate application.
 *
 * This class handles creating, playing, and releasing MediaPlayer resources for sound effects
 * throughout the application. It follows best practices for MediaPlayer lifecycle management
 * to prevent memory leaks.
 *
 * @property context The application context used to access resources
 */
class SoundManager(private val context: Context) {

    /**
     * Plays the message sent sound effect.
     */
    fun playMessageSent() {
        playSound(R.raw.message_sent)
    }

    /**
     * Plays the message received sound effect.
     */
    fun playMessageReceived() {
        playSound(R.raw.message_received)
    }

    /**
     * Plays a sound from a resource ID.
     *
     * Creates a MediaPlayer instance, sets up auto-release on completion,
     * and starts playback.
     *
     * @param soundResId Resource ID of the sound to play
     */
    private fun playSound(soundResId: Int) {
        MediaPlayer.create(context, soundResId)?.apply {
            setOnCompletionListener { it.release() }
            start()
        }
    }
}