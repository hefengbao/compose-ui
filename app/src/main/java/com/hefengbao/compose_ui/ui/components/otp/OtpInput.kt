package com.hefengbao.compose_ui.ui.components.otp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OtpInput(
    modifier: Modifier = Modifier,
    count: Int = 4,
    fontSize: TextUnit = 36.sp
) {
    val focusManager = LocalFocusManager.current
    val keyboardManager = LocalSoftwareKeyboardController.current
    var state by remember {
        mutableStateOf(OtpState(code = (1..count).map { null }))
    }

    val focusRequesters = remember { List(count){ FocusRequester() } }

    fun onAction(action: OtpAction){
        when(action){
            is OtpAction.OnChangeFieldFocused -> {
                state = state.copy(focusedIndex = action.index)
            }
            is OtpAction.OnEnterNumber -> {
                if (action.number != null){
                    focusRequesters[action.index].freeFocus()
                }
                val newCode = state.code.mapIndexed { currentIndex, currentNumber ->
                    if (currentIndex == action.index){
                        action.number
                    }else{
                        currentNumber
                    }
                }
                val wasNumberRemoved = action.number == null
                state = state.copy(
                    code = newCode,
                    focusedIndex = if (wasNumberRemoved || state.code.getOrNull(action.index) != null){
                        state.focusedIndex
                    }else{
                        if (state.focusedIndex == null){
                            null
                        }else if (state.focusedIndex == count - 1){
                            state.focusedIndex
                        }else{
                            getFirstEmptyFieldIndexAfterFocusedIndex(state.code, state.focusedIndex!!)
                        }
                    }
                )
            }
            OtpAction.OnKeyboardBack -> {
                val previousIndex = state.focusedIndex?.minus(1)?.coerceAtLeast(0)
                state = state.copy(
                    code = state.code.mapIndexed { index, number ->
                        if (index == previousIndex){
                            null
                        }else{
                            number
                        }
                    },
                    focusedIndex = previousIndex
                )
            }
        }
    }

    LaunchedEffect(state.focusedIndex) {
        state.focusedIndex?.let {
            focusRequesters.getOrNull(it)?.requestFocus()
        }
    }

    LaunchedEffect(state.code, keyboardManager) {
        val allNumberEnter = state.code.none { it == null }
       if (allNumberEnter){
           focusRequesters.forEach{
               it.freeFocus()
           }
           focusManager.clearFocus()
           keyboardManager?.hide()
       }
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        state.code.forEachIndexed { index, number ->
            OtpInputField(
                number = number,
                fontSize = fontSize,
                onKeyboardBack = {
                    onAction(OtpAction.OnKeyboardBack)
                },
                onFocusChanged = { isFocused ->
                    if (isFocused){
                        onAction(OtpAction.OnChangeFieldFocused(index))
                    }
                },
                onNumberChanged = { newNumber ->
                    onAction(OtpAction.OnEnterNumber(newNumber, index))
                },
                focusRequester = focusRequesters[index],
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
            )
        }
    }
}

private fun getFirstEmptyFieldIndexAfterFocusedIndex(
    code: List<Int?>,
    currentFocusedIndex: Int
): Int{
    code.forEachIndexed { index, number ->
        if (index <= currentFocusedIndex){
            return@forEachIndexed
        }
        if (number == null){
            return index
        }
    }

    return currentFocusedIndex
}

@Preview(showBackground = true)
@Composable
private fun OtpInputPreview() {
    OtpInput(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}