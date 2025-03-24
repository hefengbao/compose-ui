package com.hefengbao.compose_ui.ui.components.otp

data class OtpState(
    val code: List<Int?> = (1..4).map { null },
    val focusedIndex: Int? = null
)
