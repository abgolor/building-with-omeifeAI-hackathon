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

package com.ajaytechsolutions.ekotranslate.domain.model

import com.ajaytechsolutions.ekotranslate.domain.util.ResponseStatus

/**
 * Data class representing a message in the chat conversation.
 *
 * @property content The text content of the message
 * @property author Who sent the message (User or AI)
 * @property status The current status of the message
 * @property timestamp When the message was created
 * @property isLoading Whether this message represents a loading state
 */
data class ChatMessage(
    val content: String,
    val author: ChatUser,
    val status: ResponseStatus,
    val timestamp: Long = System.currentTimeMillis(),
    val isLoading: Boolean = false
)