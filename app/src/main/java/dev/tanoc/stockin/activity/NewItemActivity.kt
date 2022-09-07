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
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import dev.tanoc.stockin.App
import dev.tanoc.stockin.component.ItemForm
import dev.tanoc.stockin.ui.theme.StockinTheme
import dev.tanoc.stockin.viewmodel.NewItemViewModel
import dev.tanoc.stockin.viewmodel.NewItemViewModelFactory
import dev.tanoc.stockin.viewmodel.RealNewItemViewModel

class NewItemActivity : ComponentActivity() {
    private val viewModel by lazy {
        val appContainer = (application as App).appContainer
        val factory = NewItemViewModelFactory(
            appContainer.itemRepository,
            appContainer.titleRepository,
            appContainer.thumbnailRepository,
            appContainer.prefRepository,
            "",
            initUrl,
            "",
        )
        ViewModelProvider(this, factory).get(RealNewItemViewModel::class.java)
    }

    private val initUrl by lazy {
        if (intent?.action == Intent.ACTION_SEND) {
            intent.getStringExtra(Intent.EXTRA_TEXT) ?: ""
        } else {
            ""
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            StockinTheme {
                NewItemScreen(
                    vm = viewModel,
                    finish = { finish() },
                    showToast = {
                        Toast.makeText(this@NewItemActivity, it, Toast.LENGTH_LONG).show()
                    }
                )
            }
        }
    }
}

@Composable
fun NewItemScreen(
    vm: NewItemViewModel,
    finish: () -> Unit,
    showToast: (String) -> Unit,
) {
    LaunchedEffect(vm.effect) {
        vm.effect.collect { effect ->
            when (effect) {
                is NewItemViewModel.Effect.Finish -> {
                    finish()
                }
                is NewItemViewModel.Effect.ShowToast -> {
                    showToast(effect.message)
                }
            }
        }
    }

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
            NewItemForm(vm)
        }
    }
}

@Composable
fun NewItemForm(vm: NewItemViewModel) {
    val state by vm.state.collectAsState()

    val onTitleChanged = { input: String ->
        vm.event(NewItemViewModel.Event.ChangeTitle(input))
    }
    val onUrlChanged = { input: String ->
        vm.event(NewItemViewModel.Event.ChangeUrl(input))
    }
    val onThumbnailChanged = { input: String ->
        vm.event(NewItemViewModel.Event.ChangeThumbnail(input))
    }
    val onQueryTitle = {
        vm.event(NewItemViewModel.Event.QueryUrl)
    }
    val onQueryThumbnail = {
        vm.event(NewItemViewModel.Event.QueryThumbnail)
    }
    val onSubmit = {
        vm.event(NewItemViewModel.Event.Submit)
    }

    ItemForm(
        title = state.title,
        url = state.url,
        thumbnail = state.thumbnail,
        onTitleChanged = onTitleChanged,
        onUrlChanged = onUrlChanged,
        onThumbnailChanged = onThumbnailChanged,
        onQueryTitle = onQueryTitle,
        onQueryThumbnail = onQueryThumbnail,
        onSubmit = onSubmit,
    )
}
