package com.example.twostepauthui

sealed interface OtpAction {
    data class OnEnterNumber(val number: Int?, val index: Int) : OtpAction
    data class OnChangeFieldFocus(val index : Int) : OtpAction
    data object onKeyboardBack: OtpAction
}