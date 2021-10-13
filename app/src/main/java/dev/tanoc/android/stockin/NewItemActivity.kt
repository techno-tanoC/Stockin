package dev.tanoc.android.stockin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import dev.tanoc.android.stockin.ui.theme.StockinTheme

class NewItemActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DefaultPreview()
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        StockinTheme {
            NewItemForm()
        }
    }

    @Composable
    fun NewItemForm() {
        val title = remember { mutableStateOf("") }
        val url = remember { mutableStateOf("") }

        Column {
            TextField(
                value = title.value,
                onValueChange = {
                    title.value = it
                },
                label = { Text("title") }
            )
            TextField(
                value = url.value,
                onValueChange = {
                    url.value = it
                },
                label = { Text("url") }
            )
            Button(onClick = {
            }) {
                Text("submit")
            }
        }
    }
}
