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

package com.ajaytechsolutions.ekotranslate.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ajaytechsolutions.ekotranslate.R
import com.ajaytechsolutions.ekotranslate.domain.model.ChatMessage
import com.ajaytechsolutions.ekotranslate.ui.components.ChatBubble
import com.ajaytechsolutions.ekotranslate.ui.components.MessageInput
import com.ajaytechsolutions.ekotranslate.ui.components.TypingIndicator
import com.ajaytechsolutions.ekotranslate.ui.theme.EkoTranslateTheme
import com.ajaytechsolutions.ekotranslate.utils.SoundManager
import com.ajaytechsolutions.ekotranslate.viewmodel.TranslationViewModel

/**
 * Main chat screen of the EkoTranslate application.
 * This screen displays the chat interface for translation between English and Yoruba,
 * including a welcome message with quick suggestions, chat history, and an input field.
 *
 * @param modifier Modifier to be applied to the component
 * @param viewModel ViewModel that manages the translation logic and state
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    viewModel: TranslationViewModel = viewModel()
) {
    val context = LocalContext.current
    val soundManager = remember { SoundManager(context) }

    val chatMessages by viewModel.chatMessages.collectAsState()
    val reversedMessages = chatMessages.asReversed()
    val listState = rememberLazyListState()
    val keyboardVisible = WindowInsets.isImeVisible

    val showWelcome = rememberSaveable { mutableStateOf(true) }
    val isSending = rememberSaveable { mutableStateOf(false) }

    // Scroll to the latest message when a new message arrives or keyboard visibility changes
    LaunchedEffect(reversedMessages.size, keyboardVisible) {
        if (reversedMessages.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Chat content area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            // Welcome message overlay
            Column{
                AnimatedVisibility(
                    visible = showWelcome.value,
                    enter = fadeIn(animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(300))
                ) {
                    WelcomeMessage(
                        onSuggestionClick = { suggestion ->
                            handleSuggestionClick(
                                suggestion = suggestion,
                                viewModel = viewModel,
                                soundManager = soundManager,
                                showWelcome = showWelcome,
                                isSending = isSending
                            )
                        }
                    )
                }
            }

            // Chat message list (only visible when welcome is hidden)
            if (!showWelcome.value) {
                ChatMessageList(
                    messages = reversedMessages,
                    listState = listState
                )
            }
        }

        // Message input (always visible at the bottom)
        MessageInput(
            isSending = isSending,
            welcomeMessage = showWelcome,
            onCancelMessage = { viewModel.cancelActiveTranslation() }
        ) { userMessage, onMessageSent ->
            handleUserMessage(
                message = userMessage,
                onMessageSent = onMessageSent,
                viewModel = viewModel,
                soundManager = soundManager,
                showWelcome = showWelcome,
                isSending = isSending
            )
        }
    }
}

/**
 * Handles the sending of messages from user input.
 *
 * @param message The message text to send
 * @param onMessageSent Callback to update UI state after sending
 * @param viewModel The ViewModel that handles translation
 * @param soundManager Utility to play sounds
 * @param showWelcome State controlling welcome message visibility
 * @param isSending State tracking if a message is currently being sent
 */
private fun handleUserMessage(
    message: String,
    onMessageSent: (Boolean) -> Unit,
    viewModel: TranslationViewModel,
    soundManager: SoundManager,
    showWelcome: MutableState<Boolean>,
    isSending: MutableState<Boolean>
) {
    soundManager.playMessageSent()

    viewModel.sendMessage(
        userMessage = message,
        sourceLanguage = "english",
        targetLanguage = "yoruba",
        onTranslationStateChange = { successful ->
            onMessageSent(successful)
            if (!successful) isSending.value = false
        }
    ) {
        if (showWelcome.value) showWelcome.value = false
        soundManager.playMessageReceived()
    }
}

/**
 * Handles clicks on suggestion buttons from the welcome screen.
 *
 * @param suggestion The suggestion text that was clicked
 * @param viewModel The ViewModel that handles translation
 * @param soundManager Utility to play sounds
 * @param showWelcome State controlling welcome message visibility
 * @param isSending State tracking if a message is currently being sent
 */
private fun handleSuggestionClick(
    suggestion: String,
    viewModel: TranslationViewModel,
    soundManager: SoundManager,
    showWelcome: MutableState<Boolean>,
    isSending: MutableState<Boolean>
) {
    soundManager.playMessageSent()

    viewModel.sendMessage(
        userMessage = suggestion,
        sourceLanguage = "english",
        targetLanguage = "yoruba",
        onTranslationStateChange = { isLoading ->
            showWelcome.value = false
            isSending.value = isLoading
        }
    ) {
        soundManager.playMessageReceived()
    }
}

/**
 * Displays the list of chat messages.
 *
 * @param messages List of messages to display
 * @param listState State of the lazy list for scrolling
 */
@Composable
private fun ChatMessageList(
    messages: List<ChatMessage>,
    listState: LazyListState
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        reverseLayout = true
    ) {
        items(messages) { chatMessage ->
            if (chatMessage.isLoading) {
                TypingIndicator()
            } else {
                ChatBubble(chatMessage)
            }
        }
    }
}

/**
 * Welcome message component shown when the chat is first opened.
 * Displays a greeting and suggestion buttons for quick start.
 *
 * @param onSuggestionClick Callback for when a suggestion button is clicked
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WelcomeMessage(onSuggestionClick: (String) -> Unit) {
    val suggestions = listOf(
        "Good morning!",
        "How are you?",
        "Thank you!",
        "Where are you?",
        "Good night!",
        "I love you!",
        "Come here!",
        "What is your name?",
        "I am hungry!",
        "See you later!"
    )

    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val isLargeScreen = screenWidthDp > 600  // Threshold for large screens

    var showAll by remember { mutableStateOf(isLargeScreen) }
    val shownSuggestions = if (showAll) {
        suggestions
    } else {
        remember { suggestions.shuffled().take(4) }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.welcome_message_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Display suggestions in a responsive grid
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            shownSuggestions.forEach { suggestion ->
                SuggestionButton(text = suggestion) {
                    onSuggestionClick(suggestion)
                }
            }

            // "More" button (only on small screens when not showing all)
            if (!showAll && !isLargeScreen) {
                SuggestionButton(
                    text = stringResource(R.string.more_button),
                    onClick = { showAll = true }
                )
            }
        }
    }
}

/**
 * Button component for translation suggestions.
 *
 * @param text The suggestion text to display on the button
 * @param onClick Callback for when the button is clicked
 */
@Composable
fun SuggestionButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .padding(4.dp)
            .wrapContentSize(),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

/**
 * Previews for the chat screen components.
 */
@Preview(showBackground = true)
@Composable
fun WelcomeMessagePreview() {
    EkoTranslateTheme {
        WelcomeMessage {}
    }
}

@Preview(showBackground = true)
@Composable
fun SuggestionButtonPreview() {
    EkoTranslateTheme {
        SuggestionButton(text = "Good morning!") {}
    }
}