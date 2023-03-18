package dev.tanoc.stockin.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import dagger.hilt.android.AndroidEntryPoint
import dev.tanoc.stockin.component.ItemForm
import dev.tanoc.stockin.component.StockinScaffold
import dev.tanoc.stockin.ui.theme.StockinTheme
import dev.tanoc.stockin.viewmodel.NewItemViewModel
import dev.tanoc.stockin.viewmodel.RealNewItemViewModel
import dev.tanoc.stockin.viewmodel.use
import javax.inject.Inject

@AndroidEntryPoint
class NewItemActivity : ComponentActivity() {
    @Inject lateinit var viewModel: RealNewItemViewModel

    private val initUrl by lazy {
        if (intent?.action == Intent.ACTION_SEND) {
            intent.getStringExtra(Intent.EXTRA_TEXT) ?: ""
        } else {
            ""
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.init(initUrl)

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

    StockinScaffold {
        NewItemForm(
            title = state.title,
            url = state.url,
            thumbnail = state.thumbnail,
            dispatch,
        )
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
        dispatch(NewItemViewModel.Event.QueryTitle)
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
