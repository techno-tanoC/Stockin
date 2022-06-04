package dev.tanoc.stockin.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dev.tanoc.stockin.App
import dev.tanoc.stockin.component.ItemForm
import dev.tanoc.stockin.ui.theme.StockinTheme
import dev.tanoc.stockin.viewmodel.EditItemViewModel
import dev.tanoc.stockin.viewmodel.EditItemViewModelFactory
import kotlinx.coroutines.launch

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
        val factory = EditItemViewModelFactory(
            appContainer.itemRepository,
            appContainer.titleRepository,
            appContainer.prefRepository,
        )
        editItemViewModel = ViewModelProvider(this, factory).get(EditItemViewModel::class.java)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                editItemViewModel.event.collect {
                    Toast.makeText(this@EditItemActivity, it, Toast.LENGTH_LONG).show()
                }
            }
        }

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
            Column(
                modifier = Modifier
                    .padding(it)
            ) {
                Form()
            }
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
            editItemViewModel.query(url.value) {
                title.value = it.title
            }
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
