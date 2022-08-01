package com.oguzhanaslann.gesturenavigation

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.window.OnBackInvokedCallback
import android.window.OnBackInvokedDispatcher
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oguzhanaslann.gesturenavigation.ui.theme.GestureNavigationTheme


private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            // Your business logic to handle the back pressed event
            Log.d(TAG, "onBackPressedCallback: handleOnBackPressed")
        }
    }

    private val onBackInvokedCallback = if (Build.VERSION.SDK_INT >= 33) {
        OnBackInvokedCallback {
            Log.d(TAG, "onBackInvokedCallback: onBackInvoked")
        }
    } else {
        null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setMainPageContentViews()
//        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }
    // onBackPressedDispatcher comes into

    private fun setMainPageContentViews() {
        setContent {
            GestureNavigationTheme {
                // A surface container using the 'background' color from the theme
                var isEnabled by remember { mutableStateOf(false) }
                var isRegistered by remember { mutableStateOf(false) }

                SideEffect {
                    onBackPressedCallback.isEnabled = isEnabled
                }

                if (Build.VERSION.SDK_INT >= 33) { // ABOVE android 13
                    SideEffect {
                        if (isRegistered) {
                            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                                OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                                onBackInvokedCallback!!
                            )
                        } else {
                            onBackInvokedDispatcher.unregisterOnBackInvokedCallback(
                                onBackInvokedCallback!!
                            )
                        }
                    }
                }


                MainView(
                    onBackPressedCallBack = {
                        /*deprecated on api 33 and above*/
                        //                        this.onBackPressed()

                        this.onBackPressedDispatcher.onBackPressed()
                    },
                    isEnabled = isEnabled,
                    onToggleBackPressedCallback = {
                        /*
                        * callback will override legacy onBackPressed()
                        *  and will be called when back button is pressed when isEnabled is true
                        *
                        * when isEnabled is false, system will handle back button press
                        *
                        * remember this callback backward compatible
                        *
                        * */
                        isEnabled = !isEnabled
                    },
                    isRegistered = isRegistered,
                    onToggleBackInvokedCallback = {
                        isRegistered = !isRegistered
                    }

                )
            }
        }
    }

    override fun onBackPressed() {
        /** deprecated on api 33 and above will not be called @see {@link super.onBackPressed}  */
        super.onBackPressed()
        Log.d(TAG, "onBackPressed")
    }
}

@Composable
fun MainView(
    onBackPressedCallBack: () -> Unit = {},
    isEnabled: Boolean = true,
    onToggleBackPressedCallback: (Boolean) -> Unit = {},
    isRegistered: Boolean = true,
    onToggleBackInvokedCallback: (Boolean) -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { onBackPressedCallBack() }) {
                Text("Press to go back")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "BackPressedCallback: isEnabled: $isEnabled")
                Switch(
                    checked = isEnabled,
                    onCheckedChange = onToggleBackPressedCallback
                )
            }

            if (Build.VERSION.SDK_INT >= 33) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "BackInvokedCallback: isRegistered: $isRegistered")
                    Switch(
                        checked = isRegistered,
                        onCheckedChange = onToggleBackInvokedCallback
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = when {
                    isEnabled -> "onBackPressedCallback is going to handle back navigation"
                    isRegistered -> "onBackInvokedCallback is going to handle back navigation but not button press"
                    else -> "System is going to handle back press and back gesture"
                },
                textAlign = TextAlign.Center,
                fontSize = 12.sp
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview() {
    GestureNavigationTheme {
        MainView()
    }
}
