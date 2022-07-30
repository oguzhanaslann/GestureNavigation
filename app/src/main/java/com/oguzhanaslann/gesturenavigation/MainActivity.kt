package com.oguzhanaslann.gesturenavigation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oguzhanaslann.gesturenavigation.ui.theme.GestureNavigationTheme


private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {

        override fun handleOnBackPressed() {
            // Your business logic to handle the back pressed event
            Log.d(TAG, "onBackPressedCallback: handleOnBackPressed")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GestureNavigationTheme {
                // A surface container using the 'background' color from the theme
                var isEnabled by remember { mutableStateOf(false) }

                SideEffect {
                    onBackPressedCallback.isEnabled = isEnabled
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
                    }

                )
            }
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onBackPressed() {
        /*deprecated on api 33 and above*/
        super.onBackPressed()
        Log.d(TAG, "onBackPressed")
    }
}

@Composable
fun MainView(
    onBackPressedCallBack: () -> Unit = {},
    isEnabled: Boolean = true,
    onToggleBackPressedCallback: (Boolean) -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        ) {
            Button(onClick = { onBackPressedCallBack() }) {
                Text("OnBackPressed")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "isEnabled: $isEnabled")
                Switch(
                    checked = isEnabled,
                    onCheckedChange = onToggleBackPressedCallback
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview() {
    GestureNavigationTheme {
        MainView()
    }
}
