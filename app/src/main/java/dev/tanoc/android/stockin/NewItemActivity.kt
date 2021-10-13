package dev.tanoc.android.stockin

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import dev.tanoc.android.stockin.ui.theme.StockinTheme
import dev.tanoc.android.stockin.viewmodel.NewItemViewModel

class NewItemActivity : ComponentActivity() {
    private val model: NewItemViewModel by viewModels()

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
            Container()
        }
    }

    @Composable
    fun Container() {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("New Item") },
                )
            },
        ) {
            NewItemForm()
        }
    }

    @Composable
    fun NewItemForm() {
        val title = remember { mutableStateOf("") }
        val url = remember { mutableStateOf("") }

        model.item.observe(this, Observer {
            val intent = Intent()
            intent.putExtra("id", it.id)
            intent.putExtra("title", it.title)
            intent.putExtra("url", it.url)
            setResult(RESULT_OK, intent)
            finish()
        })

        Column(
            Modifier
                .padding(8.dp),
        ) {
            OutlinedTextField(
                value = title.value,
                onValueChange = { title.value = it },
                label = { Text("Title") },
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
            )
            OutlinedTextField(
                value = url.value,
                onValueChange = { url.value = it },
                label = { Text("Url") },
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
            )
            Button(
                onClick = { model.create(title.value, url.value) },
                modifier = Modifier
                    .padding(4.dp, 8.dp)
                    .fillMaxWidth(),
            ) {
                Text(
                    text = "Submit",
                    modifier = Modifier.padding(12.dp),
                )
            }
        }
    }
}
