package dev.tanoc.stockin.activity

import android.content.Intent
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
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dev.tanoc.stockin.App
import dev.tanoc.stockin.component.ItemForm
import dev.tanoc.stockin.ui.theme.StockinTheme
import dev.tanoc.stockin.viewmodel.NewItemViewModel
import dev.tanoc.stockin.viewmodel.NewItemViewModelFactory
import kotlinx.coroutines.launch

class NewItemActivity : ComponentActivity() {
    private lateinit var newItemViewModel: NewItemViewModel

    private val initUrl by lazy {
        if (intent?.action == Intent.ACTION_SEND) {
            intent.getStringExtra(Intent.EXTRA_TEXT) ?: ""
        } else {
            ""
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer = (application as App).appContainer
        val factory = NewItemViewModelFactory(
            appContainer.itemRepository,
            appContainer.titleRepository,
            appContainer.prefRepository,
            "",
            initUrl,
        )
        newItemViewModel = ViewModelProvider(this, factory).get(NewItemViewModel::class.java)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                newItemViewModel.event.collect {
                    Toast.makeText(this@NewItemActivity, it, Toast.LENGTH_LONG).show()
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
        val isFinish = newItemViewModel.isFinish.collectAsState()
        if (isFinish.value) {
            finish()
        }

        val title = newItemViewModel.title.collectAsState()
        val url = newItemViewModel.url.collectAsState()

        val onTitleChanged = { input: String ->
            newItemViewModel.updateTitle(input)
        }
        val onUrlChanged = { input: String ->
            newItemViewModel.updateUrl(input)
        }
        val onQueryTitle = {
            newItemViewModel.query(url.value)
        }
        val onSubmit = {
            newItemViewModel.submit(title.value, url.value)
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
