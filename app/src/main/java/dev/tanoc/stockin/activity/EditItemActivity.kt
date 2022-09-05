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
import androidx.lifecycle.ViewModelProvider
import dev.tanoc.stockin.App
import dev.tanoc.stockin.component.ItemForm
import dev.tanoc.stockin.ui.theme.StockinTheme
import dev.tanoc.stockin.viewmodel.EditItemViewModel
import dev.tanoc.stockin.viewmodel.RealEditItemViewModel
import dev.tanoc.stockin.viewmodel.EditItemViewModelFactory

class EditItemActivity : ComponentActivity() {
    private val viewModel by lazy {
        val appContainer = (application as App).appContainer
        val factory = EditItemViewModelFactory(
            appContainer.itemRepository,
            appContainer.titleRepository,
            appContainer.thumbnailRepository,
            appContainer.prefRepository,
            initId,
            initTitle,
            initUrl,
            initThumbnail,
        )
        ViewModelProvider(this, factory).get(RealEditItemViewModel::class.java)
    }

    private val initId by lazy {
        intent.getStringExtra("id") ?: ""
    }
    private val initTitle by lazy {
        intent.getStringExtra("title") ?: ""
    }
    private val initUrl by lazy {
        intent.getStringExtra("url") ?: ""
    }
    private val initThumbnail by lazy {
        intent.getStringExtra("thumbnail") ?: ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            StockinTheme {
                EditItemScreen(
                    vm = viewModel,
                    finish = { finish() },
                    showToast = {
                        Toast.makeText(this@EditItemActivity, it, Toast.LENGTH_LONG).show()
                    }
                )
            }
        }
    }
}

@Composable
fun EditItemScreen(
    vm: EditItemViewModel,
    finish: () -> Unit,
    showToast: (String) -> Unit
) {
    LaunchedEffect(vm.effect) {
        vm.effect.collect { effect ->
            when (effect) {
                is EditItemViewModel.Effect.Finish -> {
                    finish()
                }
                is EditItemViewModel.Effect.ShowToast -> {
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
            EditItemForm(vm)
        }
    }
}

@Composable
fun EditItemForm(vm: EditItemViewModel) {
    val state by vm.state.collectAsState()

    val onTitleChanged = { input: String ->
        vm.event(EditItemViewModel.Event.ChangeTitle(input))
    }
    val onUrlChanged = { input: String ->
        vm.event(EditItemViewModel.Event.ChangeUrl(input))
    }
    val onThumbnailChanged = { input: String ->
        vm.event(EditItemViewModel.Event.ChangeThumbnail(input))
    }
    val onQueryTitle = {
        vm.event(EditItemViewModel.Event.QueryUrl)
    }
    val onQueryThumbnail = {
        vm.event(EditItemViewModel.Event.QueryThumbnail)
    }
    val onSubmit = {
        vm.event(EditItemViewModel.Event.Submit)
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
