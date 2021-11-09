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
import dev.tanoc.android.stockin.model.EventObserver
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
        model.message.observe(this, EventObserver {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        })

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

        val title = model.title.observeAsState("")
        val url = model.url.observeAsState("")

        val onTitleChanged = { title: String ->
            model.updateTitle(title)
        }
        val onUrlChanged = { url: String ->
            model.updateUrl(url)
        }
        val onSubmit = {
            model.submit()
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("New Item") },
                )
            },
        ) {
            ItemForm(title.value, url.value, onTitleChanged, onUrlChanged, onSubmit)
        }
    }
}
