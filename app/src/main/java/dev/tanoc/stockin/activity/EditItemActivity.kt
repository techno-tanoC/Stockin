package dev.tanoc.stockin.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import dev.tanoc.stockin.App
import dev.tanoc.stockin.component.ItemForm
import dev.tanoc.stockin.ui.theme.StockinTheme
import dev.tanoc.stockin.viewmodel.EditItemViewModel

class EditItemActivity : ComponentActivity() {
    private lateinit var editItemViewModel: EditItemViewModel

    private val initId by lazy {
        intent.getLongExtra("id", -1)
    }
    private  val initTitle by lazy {
        intent.getStringExtra("title") ?: ""
    }
    private val initUrl by lazy {
        intent.getStringExtra("url") ?: ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer = (application as App).appContainer
        editItemViewModel = EditItemViewModel(appContainer.itemRepository, appContainer.prefRepository)

        setContent {
            StockinTheme {
                View()
            }
        }
    }

    @Composable
    fun View() {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("Stockin")
                    },
                )
            },
        ) {
            Form()
        }
    }

    @Composable
    fun Form() {
        val isFinish = editItemViewModel.isFinish.collectAsState()
        if (isFinish.value) {
            finish()
        }

        val title = remember { mutableStateOf(initTitle) }
        val url = remember { mutableStateOf(initUrl) }

        val onTitleChanged = { input: String ->
            title.value = input
        }
        val onUrlChanged = { input: String ->
            url.value = input
        }
        val onQueryTitle = {
        }
        val onSubmit = {
            editItemViewModel.update(initId, title.value, url.value)
        }

        ItemForm(
            title = title.value,
            url = url.value,
            onTitleChanged = onTitleChanged,
            onUrlChanged = onUrlChanged,
            onQueryTitle = onQueryTitle,
            onSubmit = onSubmit,
        )
    }
}
