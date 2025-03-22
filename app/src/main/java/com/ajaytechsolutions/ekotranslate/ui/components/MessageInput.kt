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

package com.ajaytechsolutions.ekotranslate.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.InputType
import android.view.View
import android.view.View.TEXT_DIRECTION_ANY_RTL
import android.view.View.TEXT_DIRECTION_LOCALE
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.children
import androidx.core.widget.doOnTextChanged
import com.ajaytechsolutions.ekotranslate.R
import com.ajaytechsolutions.ekotranslate.ui.theme.EkoTranslateTheme
import timber.log.Timber
import java.lang.reflect.Field

/**
 * Saver for [TextRange] to preserve selection state across configuration changes.
 */
val TextRangeSaver = Saver<TextRange, List<Int>>(
    save = { listOf(it.start, it.end) },
    restore = { TextRange(it[0], it[1]) }
)

/**
 * Custom layout that resolves focus search issues in nested views.
 * This prevents focus from unexpectedly moving to other elements in the view hierarchy.
 */
class MessageInputFocusLayout(context: Context) : FrameLayout(context) {
    override fun focusSearch(focused: View?, direction: Int): View? {
        return if (focused != null) {
            super.focusSearch(focused, direction)
        } else {
            null
        }
    }
}


@Composable
fun MessageInput(
    modifier: Modifier = Modifier,
    isSending: MutableState<Boolean>,
    welcomeMessage: MutableState<Boolean>,
    placeholder: String = stringResource(R.string.enter_text_to_translate),
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    hintColor: Color = Color.Gray,
    onCancelMessage: () -> Unit,
    onSendMessage: (String, (Boolean) -> Unit) -> Unit,
) {
    // State management
    var text by rememberSaveable { mutableStateOf("") }
    var selection = rememberSaveable(stateSaver = TextRangeSaver) {
        mutableStateOf(TextRange(0, 0))
    }
    var showKeyboard by rememberSaveable { mutableStateOf(false) }
    var freeFocus by rememberSaveable { mutableStateOf(false) }
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    val onTextChanged: (String) -> Unit = { newText ->
        text = newText
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 4.dp, start = 8.dp, end = 8.dp, bottom = 8.dp),
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Text input field
                    AndroidView(
                        modifier = Modifier.weight(1f),
                        factory = { context ->
                            createEditText(context, text, selection, textColor, isRtl, placeholder, hintColor)
                        }
                    ) { viewGroup ->
                        updateEditText(
                            viewGroup = viewGroup,
                            text = text,
                            selection = selection,
                            textColor = textColor,
                            hintColor = hintColor,
                            placeholder = placeholder,
                            showKeyboard = showKeyboard,
                            freeFocus = freeFocus,
                            isSending = isSending.value,
                            onShowKeyboardComplete = { showKeyboard = false },
                            onFreeFocusComplete = { freeFocus = false },
                            onTextChanged = onTextChanged
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Action button container
                    Box(
                        modifier = Modifier.align(Alignment.Bottom)
                    ) {
                        when {
                            // Send button when text is not empty and not currently sending
                            text.isNotBlank() && !isSending.value -> {
                                ActionButton(
                                    icon = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Send",
                                    onClick = {
                                        if (welcomeMessage.value) welcomeMessage.value = false
                                        onSendMessage(text) { isLoading ->
                                            isSending.value = isLoading
                                            text = ""
                                            selection.value = TextRange(0, 0)
                                        }
                                    }
                                )
                            }
                            // Cancel button when sending is in progress
                            isSending.value -> {
                                ActionButton(
                                    icon = Icons.Filled.Close,
                                    contentDescription = "Stop",
                                    onClick = {
                                        onCancelMessage()
                                        isSending.value = false
                                    }
                                )
                            }
                            // Invisible placeholder to maintain layout
                            else -> {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .alpha(0f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Creates an EditText wrapped in a focus management layout.
 *
 * @param context Android context
 * @param text Current text content
 * @param selection State current text selection
 * @param textColor Color for the text
 * @param isRtl Whether the layout is right-to-left
 * @param placeholder Hint text
 * @param hintColor Color for the placeholder
 * @return A ViewGroup containing the configured EditText
 */
@SuppressLint("AppCompatCustomView")
private fun createEditText(
    context: Context,
    text: String,
    selection: MutableState<TextRange>,
    textColor: Color,
    isRtl: Boolean,
    placeholder: String,
    hintColor: Color
): ViewGroup {
    // Create custom EditText with selection handling
    val editText = object : EditText(context) {
        override fun onSelectionChanged(selStart: Int, selEnd: Int) {
            val start = minOf(text.length, minOf(selStart, selEnd))
            val end = minOf(text.length, maxOf(selStart, selEnd))
            selection.value = TextRange(start, end)
            super.onSelectionChanged(start, end)
        }
    }

    editText.apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        maxLines = 5
        minLines = 1
        inputType = InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or
                InputType.TYPE_TEXT_FLAG_MULTI_LINE or
                InputType.TYPE_TEXT_FLAG_AUTO_CORRECT or InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE

        setHorizontallyScrolling(false)
        setTextColor(textColor.toArgb())
        textSize = 16.0f
        background = Color.Transparent.toArgb().toDrawable()
        textDirection = if (isRtl) TEXT_DIRECTION_LOCALE else TEXT_DIRECTION_ANY_RTL
        setPadding(0, 20, 0, 20)
        setText(text)
        setSelection(selection.value.start, selection.value.end)
        hint = placeholder
        setHintTextColor(hintColor.toArgb())

        // Apply custom font
        val typeface = ResourcesCompat.getFont(context, R.font.nunito)
        setTypeface(typeface)

        // Set cursor color
        setCursorColor(textColor)

        onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // Request keyboard to be shown
                requestFocus()
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    // Wrap in custom focus layout and return
    val workaround = MessageInputFocusLayout(context)
    workaround.addView(editText)
    workaround.layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    return workaround
}

/**
 * Updates an existing EditText with new values.
 *
 * @param viewGroup The ViewGroup containing the EditText
 * @param text Current text content
 * @param selection Current text selection
 * @param textColor Color for the text
 * @param hintColor Color for the placeholder
 * @param placeholder Hint text
 * @param showKeyboard Whether to show the keyboard
 * @param freeFocus Whether to clear focus
 * @param isSending Whether sending is in progress
 * @param onShowKeyboardComplete Callback when keyboard showing is complete
 * @param onFreeFocusComplete Callback when focus clearing is complete
 */
private fun updateEditText(
    viewGroup: ViewGroup,
    text: String,
    selection: MutableState<TextRange>,
    textColor: Color,
    hintColor: Color,
    placeholder: String,
    showKeyboard: Boolean,
    freeFocus: Boolean,
    isSending: Boolean,
    onShowKeyboardComplete: () -> Unit,
    onFreeFocusComplete: () -> Unit,
    onTextChanged: (String) -> Unit
) {
    val editText = viewGroup.children.first() as EditText
    editText.apply {
        setTextColor(textColor.toArgb())
        setHintTextColor(hintColor.toArgb())
        hint = placeholder
        textSize = 16.0f
        isFocusable = true
        isFocusableInTouchMode = isFocusable

        // Update text and selection if needed
        if (text != this.text.toString() || selection.value.start != selectionStart || selection.value.end != selectionEnd) {
            setText(text)
            setSelection(text.length)
        }

        // Show keyboard if requested
        if (showKeyboard) {
            requestFocus()
            val imm: InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
            onShowKeyboardComplete()
        }

        // Clear focus if requested
        if (freeFocus) {
            clearFocus()
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(windowToken, 0)
            onFreeFocusComplete()
        }

        // Set up text change listener if not already done
        if (tag != "listenerSet") {
            doOnTextChanged { textContent, _, _, _ ->
                if (!isSending) {
                    onTextChanged(textContent.toString())
                    selection.value = TextRange(text.length)
                } else if (textContent.toString() != text) {
                    setText(text)
                    setSelection(text.length) // Ensure cursor is always at the end
                }
            }
            tag = "listenerSet"
        }
    }
}

/**
 * Sets the cursor color for an EditText.
 * Uses reflection for API levels below 29 as a fallback.
 *
 * @param textColor The color to apply to the cursor
 */
private fun EditText.setCursorColor(textColor: Color) {
    if (Build.VERSION.SDK_INT >= 29) {
        textCursorDrawable?.let { DrawableCompat.setTint(it, textColor.toArgb()) }
    } else {
        try {
            val field: Field = TextView::class.java.getDeclaredField("mCursorDrawableRes")
            field.isAccessible = true
            field.set(this, R.drawable.custom_cursor)
        } catch (e: Exception) {
            Timber.tag("MessageInput").e("Failed to set cursor color: ${e.message}")
        }
    }
}

/**
 * Creates an action button (send or cancel) with the specified icon and action.
 *
 * @param icon The icon to display
 * @param contentDescription Accessibility description
 * @param onClick Action to perform when clicked
 */
@Composable
private fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = LocalIndication.current
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

/**
 * Preview for the MessageInput component in different states.
 */
@Preview(showBackground = true)
@Composable
fun MessageInputPreview() {
    EkoTranslateTheme {
        Column {
            // Empty state
            MessageInput(
                isSending = remember { mutableStateOf(false) },
                welcomeMessage = remember { mutableStateOf(true) },
                onCancelMessage = { },
                onSendMessage = { _, _ -> }
            )

            Spacer(modifier = Modifier.size(16.dp))

            // Sending state
            MessageInput(
                isSending = remember { mutableStateOf(true) },
                welcomeMessage = remember { mutableStateOf(false) },
                onCancelMessage = { },
                onSendMessage = { _, _ -> }
            )
        }
    }
}