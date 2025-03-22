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

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ajaytechsolutions.ekotranslate.R
import com.ajaytechsolutions.ekotranslate.domain.model.ChatMessage
import com.ajaytechsolutions.ekotranslate.domain.model.ChatUser
import com.ajaytechsolutions.ekotranslate.domain.util.ResponseStatus

/**
 * A Composable function representing a chat bubble in the chat UI.
 *
 * @param message The chat message to be displayed inside the bubble.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.author == ChatUser.USER
    val backgroundColor = if (isUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
    val textColor = if (isUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
    val shape = getBubbleShape(isUser)

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    val bubblePadding = getBubblePadding()

    val messageText = getMessageText(message)

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = if (isUser || message.status.isError()) 8.dp else 0.dp)
                .padding(start = if (isUser) bubblePadding else 0.dp, end = if (isUser) 0.dp else bubblePadding)
                .wrapContentSize(if (isUser) Alignment.CenterEnd else Alignment.CenterStart)
                .combinedClickable(
                    onClick = {},
                    onLongClick = {
                        if (shouldAllowCopy(message)) {
                            clipboardManager.setText(AnnotatedString(message.content))
                            Toast.makeText(context, context.getString(R.string.message_copied), Toast.LENGTH_SHORT).show()
                        }
                    }
                )
        ) {
            Column {
                Text(
                    text = messageText,
                    style = if (message.status.isError()) MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic) else MaterialTheme.typography.bodyMedium,
                    color = if (message.status.isError()) Color.Gray else textColor,
                    modifier = Modifier
                        .clip(shape)
                        .background(backgroundColor)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }

        if (shouldShowCopyHint(message)) {
            Text(
                text = stringResource(R.string.long_press_to_copy),
                color = Color.Gray,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(top = 2.dp, end = 4.dp, start = 8.dp, bottom = 8.dp)
            )
        }
    }
}

/**
 * Determines the appropriate shape for the chat bubble.
 */
@Composable
private fun getBubbleShape(isUser: Boolean) = if (isUser) {
    RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp)
} else {
    RoundedCornerShape(18.dp, 18.dp, 18.dp, 4.dp)
}

/**
 * Determines padding based on screen width.
 */
@Composable
private fun getBubblePadding(): Dp {
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    return when {
        screenWidthDp < 400 -> 48.dp // Small phones
        screenWidthDp < 500 -> 56.dp // Medium phones
        else -> 64.dp // Large phones / tablets
    }
}

/**
 * Returns the appropriate message text based on status.
 */
@Composable
private fun getMessageText(message: ChatMessage): String {
    return when (message.status) {
        ResponseStatus.FAILED -> stringResource(R.string.error_translation)
        ResponseStatus.CANCELLED -> stringResource(R.string.translation_cancelled)
        else -> message.content
    }
}

/**
 * Checks if the message should allow copying.
 */
private fun shouldAllowCopy(message: ChatMessage): Boolean {
    return message.author == ChatUser.OMEIFE_AI &&
            !message.isLoading &&
            message.status !in listOf(ResponseStatus.CANCELLED, ResponseStatus.FAILED)
}

/**
 * Determines whether the copy hint should be displayed.
 */
private fun shouldShowCopyHint(message: ChatMessage): Boolean {
    return shouldAllowCopy(message)
}

/**
 * Checks if the response status is an error state.
 */
private fun ResponseStatus.isError(): Boolean {
    return this == ResponseStatus.CANCELLED || this == ResponseStatus.FAILED
}

/**
 * Preview for ChatBubble Composable.
 */
@Preview(showBackground = true)
@Composable
fun ChatBubblePreview() {
    Column(modifier = Modifier.padding(16.dp)) {
        ChatBubble(
            message = ChatMessage(
                content = "Hello, how are you?",
                author = ChatUser.USER,
                status = ResponseStatus.SUCCESS
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        ChatBubble(
            message = ChatMessage(
                content = "I'm fine, thank you!",
                author = ChatUser.OMEIFE_AI,
                status = ResponseStatus.SUCCESS
            )
        )
    }
}