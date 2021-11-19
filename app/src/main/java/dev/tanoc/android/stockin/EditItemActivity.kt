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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import dev.tanoc.android.stockin.composable.ItemForm
import dev.tanoc.android.stockin.model.EventObserver
import dev.tanoc.android.stockin.ui.theme.StockinTheme
import dev.tanoc.android.stockin.viewmodel.EditItemViewModel

class EditItemActivity : ComponentActivity() {
    private val model: EditItemViewModel by viewModels()
    private val initId: Long by lazy {
        intent.getLongExtra("id", 0)
    }
    private val initTitle: String by lazy {
        intent.getStringExtra("title") ?: ""
    }
    private val initUrl: String by lazy {
        intent.getStringExtra("url") ?: ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        model.message.observe(this, EventObserver {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        })

        model.item.observe(this, EventObserver {
            val intent = Intent()
            intent.putExtra("id", it.id)
            intent.putExtra("title", it.title)
            intent.putExtra("url", it.url)
            setResult(RESULT_OK, intent)

            Toast.makeText(this, "The item is updated.", Toast.LENGTH_SHORT).show()
            finish()
        })

        val title = remember { mutableStateOf(initTitle) }
        val url = remember { mutableStateOf(initUrl) }

        model.title.observe(this, EventObserver {
            title.value = it.title
        })

        val onTitleChanged = { input: String ->
            title.value = input
        }
        val onUrlChanged = { input: String ->
            url.value = input
        }
        val onQueryTitle = {
            model.queryTitle(url.value)
        }
        val onSubmit = {
            model.submit(initId, title.value, url.value)
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Edit Item") },
                )
            },
        ) {
            ItemForm(title.value, url.value, onTitleChanged, onUrlChanged, onQueryTitle, onSubmit)
        }
    }
}
