package com.hefengbao.compose_ui.ui.components.otp

import android.view.KeyEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.traceEventEnd
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.core.text.isDigitsOnly

/**
 * 参考：
 * https://www.bilibili.com/video/BV18kfZYiE8P
 */
@Composable
fun OtpInputField(
    modifier: Modifier = Modifier,
    number: Int?,
    borderWidth: Dp = 2.dp,
    borderColor: Color = MaterialTheme.colorScheme.primary,
    color: Color = MaterialTheme.colorScheme.surface,
    fontSize: TextUnit = 36.sp,
    focusRequester: FocusRequester,
    onFocusChanged: (Boolean) -> Unit,
    onKeyboardBack: () -> Unit,
    onNumberChanged: (Int?) -> Unit,
    shape: Shape = RoundedCornerShape(6.dp)
) {
    val text by remember(number) {
        mutableStateOf(
            TextFieldValue(
                text = number?.toString().orEmpty(),
                selection = TextRange(
                    if (number != number) 1 else 0
                )
            )
        )
    }

    var isFocused by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .border(
                width = borderWidth,
                color = borderColor,
                shape = shape
            )
            .clip(shape)
            .background(color = color),
        contentAlignment = Alignment.Center
    ){
        BasicTextField(
            value = text,
            onValueChange = {newText ->
                val newNumber = newText.text
                if (newNumber.length <=1 && newNumber.isDigitsOnly()){
                    onNumberChanged(newNumber.toIntOrNull())
                }
            },
            cursorBrush = SolidColor(color),
            singleLine = true,
            textStyle = TextStyle(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Light,
                fontSize = fontSize,
                color = borderColor
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword
            ),
            modifier = Modifier
                .padding(2.dp)
                .focusRequester(focusRequester)
                .onFocusChanged {
                    isFocused = it.isFocused
                    onFocusChanged(it.isFocused)
                }
                .onKeyEvent { event->
                    val isPressDelete = event.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DEL
                    if (isPressDelete && number == null){
                        onKeyboardBack()
                    }
                    false
                },
            decorationBox = { innerTextField ->
                innerTextField()
                if (!isFocused && number == null){
                    Box (
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            text = "-",
                            color = borderColor,
                            fontSize = fontSize,
                            fontWeight = FontWeight.Light
                        )
                    }
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OtpInputFieldPreview() {
    OtpInputField(
        modifier = Modifier.size(100.dp),
        number = null,
        focusRequester = remember { FocusRequester() },
        onFocusChanged = {},
        onKeyboardBack = {},
        onNumberChanged = {}
    )
}