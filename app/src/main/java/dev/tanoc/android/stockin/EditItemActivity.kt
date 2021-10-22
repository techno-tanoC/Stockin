package dev.tanoc.android.stockin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.tooling.preview.Preview
import dev.tanoc.android.stockin.composable.ItemForm
import dev.tanoc.android.stockin.ui.theme.StockinTheme
import dev.tanoc.android.stockin.viewmodel.EditItemViewModel

class EditItemActivity : ComponentActivity() {
    private val model: EditItemViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent.getIntExtra("id", 0).let {
            model.updateId(it)
        }
        intent.getStringExtra("title")?.let {
            model.updateTitle(it)
        }
        intent.getStringExtra("url")?.let {
            model.updateUrl(it)
        }

        setContent {
            StockinTheme {
                DefaultPreview()
            }
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
        val item = model.item.observeAsState()
        item.value?.let {
            val intent = Intent()
            intent.putExtra("id", it.id)
            intent.putExtra("title", it.title)
            intent.putExtra("url", it.url)
            setResult(RESULT_OK, intent)

            Toast.makeText(this, "The item is updated.", Toast.LENGTH_SHORT).show()
            finish()
        }

        val title = model.title.observeAsState("")
        val url = model.url.observeAsState("")

        val onTitleChanged = { input: String ->
            model.updateTitle(input)
        }
        val onUrlChanged = { input: String ->
            model.updateUrl(input)
        }
        val onSubmit = {
            model.submit()
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Edit Item") },
                )
            },
        ) {
            ItemForm(title.value, url.value, onTitleChanged, onUrlChanged, onSubmit)
        }
    }

}
