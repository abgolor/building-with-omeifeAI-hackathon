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

package com.ajaytechsolutions.ekotranslate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajaytechsolutions.ekotranslate.domain.model.ChatMessage
import com.ajaytechsolutions.ekotranslate.domain.model.ChatUser
import com.ajaytechsolutions.ekotranslate.domain.model.TranslationRequest
import com.ajaytechsolutions.ekotranslate.domain.repository.TranslationRepository
import com.ajaytechsolutions.ekotranslate.domain.util.ResponseStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

/**
 * ViewModel responsible for handling translation operations and managing chat message state.
 * This component follows the MVVM architecture pattern and is designed for the OmeifeAI Competition.
 *
 * Features:
 * - Manages chat message history between user and AI
 * - Handles translation requests and responses
 * - Provides cancellation functionality for ongoing translation requests
 * - Exposes UI state through StateFlows following unidirectional data flow patterns
 */
@HiltViewModel
class TranslationViewModel @Inject constructor(
    private val repository: TranslationRepository
) : ViewModel() {

    // UI State
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)

    // Active jobs
    private var activeTranslationJob: Job? = null

    /**
     * Sends a user message for translation and handles the response.
     *
     * @param userMessage The text to translate
     * @param sourceLanguage Language code of the original text
     * @param targetLanguage Language code of the desired translation
     * @param onTranslationStateChange Callback to notify UI of translation state changes
     * @param onTranslationComplete Optional callback when translation is successfully completed
     */
    fun sendMessage(
        userMessage: String,
        sourceLanguage: String,
        targetLanguage: String,
        onTranslationStateChange: (Boolean) -> Unit,
        onTranslationComplete: () -> Unit = {}
    ) {
        // Cancel any ongoing translation
        cancelActiveTranslation()

        // Add user message to chat history
        addMessageToChat(
            message = userMessage.trim(),
            author = ChatUser.USER,
            status = ResponseStatus.SUCCESS
        )

        // Begin translation process
        _isLoading.value = true
        onTranslationStateChange(true)

        // Add loading indicator message
        addMessageToChat(
            message = "...",
            author = ChatUser.OMEIFE_AI,
            status = ResponseStatus.SUCCESS,
            isLoading = true
        )

        // Launch translation in a coroutine
        activeTranslationJob = viewModelScope.launch {
            try {
                val request = TranslationRequest(
                    text = userMessage,
                    sourceLanguage = sourceLanguage,
                    targetLanguage = targetLanguage
                )

                val result = repository.translateText(request)

                result.fold(
                    onSuccess = { translatedText ->
                        // Replace loading message with translation result
                        replaceLoadingMessageWithResult(
                            message = translatedText,
                            status = ResponseStatus.SUCCESS
                        )
                        onTranslationComplete()
                    },
                    onFailure = { error ->
                        handleTranslationError(error)
                    }
                )
            } finally {
                _isLoading.value = false
                onTranslationStateChange(false)
            }
        }
    }

    /**
     * Cancels any ongoing translation request.
     */
    fun cancelActiveTranslation() {
        activeTranslationJob?.cancel()
        activeTranslationJob = null

        // If there's a loading message, update it to show cancellation
        val messages = _chatMessages.value
        val loadingMessageIndex = messages.indexOfLast { it.isLoading }

        if (loadingMessageIndex != -1) {
            val updatedMessages = messages.toMutableList()
            updatedMessages[loadingMessageIndex] = updatedMessages[loadingMessageIndex].copy(
                content = "Translation cancelled",
                status = ResponseStatus.CANCELLED,
                isLoading = false
            )
            _chatMessages.value = updatedMessages
        }
    }

    /**
     * Adds a new message to the chat history.
     *
     * @param message The message content
     * @param author Who sent the message (User or AI)
     * @param status Status of the message
     * @param isLoading Whether this message represents a loading state
     */
    private fun addMessageToChat(
        message: String,
        author: ChatUser,
        status: ResponseStatus,
        isLoading: Boolean = false
    ) {
        _chatMessages.value = _chatMessages.value + ChatMessage(
            content = message,
            author = author,
            status = status,
            isLoading = isLoading
        )
    }

    /**
     * Replaces the current loading message with the final translation result.
     *
     * @param message The translated text
     * @param status Status of the translation
     */
    private fun replaceLoadingMessageWithResult(message: String, status: ResponseStatus) {
        _chatMessages.value = _chatMessages.value.dropLast(1) + ChatMessage(
            content = message,
            author = ChatUser.OMEIFE_AI,
            status = status
        )
    }

    /**
     * Handles translation errors based on the exception type.
     *
     * @param error The error that occurred during translation
     */
    private fun handleTranslationError(error: Throwable) {
        val status = when (error) {
            is CancellationException -> ResponseStatus.CANCELLED
            else -> ResponseStatus.FAILED
        }

        val errorMessage = when (error) {
            is CancellationException -> "Translation cancelled"
            else -> "Translation failed: ${error.message ?: "Unknown error"}"
        }

        replaceLoadingMessageWithResult(
            message = errorMessage,
            status = status
        )
    }
}