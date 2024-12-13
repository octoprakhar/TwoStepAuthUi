package com.example.twostepauthui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

private const val VALID_OTP_CODE = "1414"

class OtpViewModel: ViewModel() {

    private val _state = MutableStateFlow(OtpState())
    val state = _state.asStateFlow()

    fun onAction(action:OtpAction){
        when(action){
            is OtpAction.OnChangeFieldFocus -> {
                _state.update { it.copy(
                    focussedIndex = action.index
                ) }
            }
            is OtpAction.OnEnterNumber -> {
                enterNumber(
                    number = action.number,
                    index = action.index
                )
            }
            OtpAction.onKeyboardBack -> {
                val previousIndex = previousFocusIndex(_state.value.focussedIndex)
                _state.update {
                    it.copy(
                        code = it.code.mapIndexed { currentIndex, currentNumber ->
                            if (currentIndex == previousIndex){
                                null
                            }else{
                                currentNumber
                            }
                        },
                        focussedIndex = previousIndex
                    )
                }
            }
        }
    }

    //Function for entering number at a specific index
    private fun enterNumber(
        number: Int?,
        index: Int
    ){
        val newCode = _state.value.code.mapIndexed { currentindex, currentNumber ->
            if (currentindex == index){
                number
            }else{
                currentNumber
            }

        }

        val wasNumberRemoved = number == null

        _state.update {
            it.copy(
                code = newCode,
                focussedIndex = if (wasNumberRemoved || it.code.getOrNull(index) != null){
                    it.focussedIndex
                }else{
                    getNextFocusedTextFieldIndex(
                        currentCode = newCode,
                        currentFocusIndex = it.focussedIndex
                    )
                },
                isValid = if (newCode.none{it == null}){
                    newCode.joinToString("") == VALID_OTP_CODE
                }else{
                    null
                }
            )
        }
    }

    //Finding the previous focus index
    private fun previousFocusIndex(currentIndex: Int?) : Int? {
        return currentIndex?.minus(1)?.coerceAtMost(0) //It means if subtraction is less than 0 then return 0
    }

    private fun getNextFocusedTextFieldIndex(
        currentCode : List<Int?>,
        currentFocusIndex: Int?
    ) : Int?{
        if (currentFocusIndex == null){
            return null
        }

        //if index is the last cell
        if (currentFocusIndex == 3){
            return currentFocusIndex
        }

        return getFirstEmptyFieldIndexAfterFocusIndex(
            code = currentCode,
            currentFocusIndex = currentFocusIndex
        )
    }

    //Finding next empty field index
    private fun getFirstEmptyFieldIndexAfterFocusIndex(
        code: List<Int?>,
        currentFocusIndex: Int
    ) : Int {

        code.forEachIndexed { index, number ->
            if (index <= currentFocusIndex){
                return@forEachIndexed
            }

            if (number == null){
                return index
            }

        }

        return currentFocusIndex

    }

}