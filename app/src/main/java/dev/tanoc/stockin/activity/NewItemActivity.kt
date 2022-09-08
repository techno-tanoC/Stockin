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
import dev.tanoc.stockin.viewmodel.*

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
    val (state, effect, dispatch) = use(vm)

    LaunchedEffect(effect) {
        effect.collect { effect ->
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
            NewItemForm(
                title = state.title,
                url = state.url,
                thumbnail = state.thumbnail,
                dispatch,
            )
        }
    }
}

@Composable
fun NewItemForm(
    title: String,
    url: String,
    thumbnail: String,
    dispatch: (NewItemViewModel.Event) -> Unit,
) {
    val onTitleChanged = { input: String ->
        dispatch(NewItemViewModel.Event.ChangeTitle(input))
    }
    val onUrlChanged = { input: String ->
        dispatch(NewItemViewModel.Event.ChangeUrl(input))
    }
    val onThumbnailChanged = { input: String ->
        dispatch(NewItemViewModel.Event.ChangeThumbnail(input))
    }
    val onQueryTitle = {
        dispatch(NewItemViewModel.Event.QueryUrl)
    }
    val onQueryThumbnail = {
        dispatch(NewItemViewModel.Event.QueryThumbnail)
    }
    val onSubmit = {
        dispatch(NewItemViewModel.Event.Submit)
    }

    ItemForm(
        title = title,
        url = url,
        thumbnail = thumbnail,
        onTitleChanged = onTitleChanged,
        onUrlChanged = onUrlChanged,
        onThumbnailChanged = onThumbnailChanged,
        onQueryTitle = onQueryTitle,
        onQueryThumbnail = onQueryThumbnail,
        onSubmit = onSubmit,
    )
}
