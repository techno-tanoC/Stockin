package dev.tanoc.android.stockin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.tanoc.android.stockin.ui.theme.StockinTheme
import dev.tanoc.android.stockin.viewmodel.NewItemViewModel

class NewItemActivity : ComponentActivity() {
    private val model: NewItemViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent?.action == Intent.ACTION_SEND) {
            intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
                model.updateUrl(it)
            }
        }

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
        val title = model.title.observeAsState("")
        val url = model.url.observeAsState("")

        val item = model.item.observeAsState()
        item.value?.let {
            val intent = Intent()
            intent.putExtra("id", it.id)
            intent.putExtra("title", it.title)
            intent.putExtra("url", it.url)
            setResult(RESULT_OK, intent)

            Toast.makeText(this, "The item is created.", Toast.LENGTH_SHORT).show()
            finish()
        }

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(8.dp),
        ) {
            OutlinedTextField(
                value = title.value,
                onValueChange = { model.updateTitle(it) },
                label = { Text("Title") },
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
            )
            OutlinedTextField(
                value = url.value,
                onValueChange = { model.updateUrl(it) },
                label = { Text("Url") },
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
            )
            Button(
                onClick = { model.create() },
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
