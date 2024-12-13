package com.example.twostepauthui

import android.view.KeyEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.traceEventEnd
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import com.example.twostepauthui.ui.theme.TwoStepAuthUiTheme

//If our otp input has four boxes where we have to enter numbers, this composable corresponds
//to one box.
@Composable
fun OtpInputField(
    modifier: Modifier = Modifier,
    number: Int?, //If number is null that means the cell is empty
    focusRequester: FocusRequester, //To programmatically focus the cell
    onFocusChange: (Boolean) -> Unit, //This will tell us if the cell has focus or not
    onNumberChange: (Int?) -> Unit, //This will give us the number that is entered in the cell
    onKeyBoardBackPressed : () -> Unit, //This will be called when back button is pressed on keyboard
){

    var text by remember {
        mutableStateOf(
            TextFieldValue(
                text = number?.toString().orEmpty(),
                selection = TextRange(
                    index = if(number != null) 1 else 0
                )
            )
        )
    }

    var isFocussed by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = modifier
            .border(2.dp, Color.Black)
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ){
        BasicTextField(
            value = text,
            onValueChange = {newText ->
                val newNumber = newText.text
                if (newNumber.length <= 1 && newNumber.isDigitsOnly()){
                    text = newText
                    onNumberChange(newNumber.toIntOrNull())
                }
            },
            cursorBrush = SolidColor(Color.Green),
            singleLine = true,
            textStyle = TextStyle(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Light,
                fontSize = 36.sp,
                color = Color.Black
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword
            ),
            modifier = Modifier
                .padding(10.dp)
                .focusRequester(focusRequester)
                .onFocusChanged {
                    isFocussed = it.isFocused
                    onFocusChange(it.isFocused)
                }
                .onKeyEvent { event->
                    val didPressedDelete = event.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DEL
                    if (didPressedDelete && number == null){
                        //That means text field was empty when delete button is pressed
                        onKeyBoardBackPressed()

                    }
                    //return false to apply the default logic
                    false
                },
            decorationBox = {innerBox->
                innerBox()
                if (!isFocussed && number == null){
                    Text(
                        text = "-",
                        textAlign = TextAlign.Center,
                        fontSize = 36.sp,
                        color = Color.Black,
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize()
                    )
                }
            }

        )
    }
}

@Preview
@Composable
private fun OtpInputFieldPreview(){
    TwoStepAuthUiTheme {
        OtpInputField(
            number =  null,
            focusRequester = remember{ FocusRequester() },
            onFocusChange = {},
            onNumberChange = {},
            onKeyBoardBackPressed = {},
            modifier = Modifier.size(100.dp)
        )
    }
}