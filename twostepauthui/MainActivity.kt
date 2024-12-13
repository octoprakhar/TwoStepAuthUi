package com.example.twostepauthui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.twostepauthui.ui.theme.TwoStepAuthUiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TwoStepAuthUiTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val viewModel = viewModel<OtpViewModel>()
                    val state by viewModel.state.collectAsStateWithLifecycle()
                    val focusRequesters = remember {
                        List(4){FocusRequester()}
                    }

                    //clear the focus when all numbers are entered
                    val focusManager = LocalFocusManager.current
                    val keyboardManager = LocalSoftwareKeyboardController.current

                    //Update ui when focus is changed
                    LaunchedEffect(state.focussedIndex){
                        state.focussedIndex?.let { index->
                            focusRequesters.getOrNull(index)?.requestFocus()
                        }
                    }

                    //Closing the keyboard when all numbers are entered
                    LaunchedEffect(state.code, keyboardManager) {
                        val allNumbersEntered = state.code.all { it != null }
                        if (allNumbersEntered){
                            focusRequesters.forEach{
                                it.freeFocus()
                            }
                            focusManager.clearFocus()
                            keyboardManager?.hide()
                        }
                    }
                    OtpScreen(
                        modifier = Modifier.padding(innerPadding).consumeWindowInsets(innerPadding),
                        state = state,
                        onAction = {action->
                            when(action){
                                is OtpAction.OnEnterNumber -> {
                                    //If it is last number entered then reset the focus and close the keyboard
                                    if(action.number != null){
                                        focusRequesters[action.index].freeFocus()
                                    }
                                }
                                else -> Unit
                            }

                            viewModel.onAction(action)

                        },
                        focusRequesters = focusRequesters
                    )
                }
            }
        }
    }
}

//28:00

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TwoStepAuthUiTheme {
        Greeting("Android")
    }
}