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
import androidx.compose.runtime.*
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
    private val viewModel by lazy {
        val appContainer = (application as App).appContainer
        val factory = EditItemViewModelFactory(
            appContainer.itemRepository,
            appContainer.titleRepository,
            appContainer.prefRepository,
            initTitle,
            initUrl,
        )
        ViewModelProvider(this, factory).get(EditItemViewModel::class.java)
    }

    private val initId by lazy {
        intent.getStringExtra("id") ?: ""
    }
    private  val initTitle by lazy {
        intent.getStringExtra("title") ?: ""
    }
    private val initUrl by lazy {
        intent.getStringExtra("url") ?: ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collect {
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
        val isFinish = viewModel.isFinish.collectAsState()
        if (isFinish.value) {
            finish()
        }

        val title = viewModel.title.collectAsState()
        val url = viewModel.url.collectAsState()

        val onTitleChanged = { input: String ->
            viewModel.updateTitle(input)
        }
        val onUrlChanged = { input: String ->
            viewModel.updateUrl(input)
        }
        val onQueryTitle = {
            viewModel.query(url.value)
        }
        val onSubmit = {
            viewModel.submit(initId, title.value, url.value)
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
