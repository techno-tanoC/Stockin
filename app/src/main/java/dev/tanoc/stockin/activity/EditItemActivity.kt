package dev.tanoc.stockin.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import dagger.hilt.android.AndroidEntryPoint
import dev.tanoc.stockin.App
import dev.tanoc.stockin.component.ItemForm
import dev.tanoc.stockin.component.StockinScaffold
import dev.tanoc.stockin.ui.theme.StockinTheme
import dev.tanoc.stockin.viewmodel.EditItemViewModel
import dev.tanoc.stockin.viewmodel.RealEditItemViewModel
import dev.tanoc.stockin.viewmodel.use
import javax.inject.Inject

@AndroidEntryPoint
class EditItemActivity : ComponentActivity() {
    @Inject lateinit var viewModel: RealEditItemViewModel

    private val initId by lazy {
        intent.getStringExtra(App.ID) ?: ""
    }
    private val initTitle by lazy {
        intent.getStringExtra(App.TITLE) ?: ""
    }
    private val initUrl by lazy {
        intent.getStringExtra(App.URL) ?: ""
    }
    private val initThumbnail by lazy {
        intent.getStringExtra(App.THUMBNAIL) ?: ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.init(initId, initTitle, initUrl, initThumbnail)

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
    val (state, effect, dispatch) = use(vm)

    LaunchedEffect(effect) {
        effect.collect { effect ->
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

    StockinScaffold {
        EditItemForm(
            title = state.title,
            url = state.url,
            thumbnail = state.thumbnail,
            isLoading = state.isLoading,
            dispatch = dispatch,
        )
    }
}

@Composable
fun EditItemForm(
    title: String,
    url: String,
    thumbnail: String,
    isLoading: Boolean,
    dispatch: (EditItemViewModel.Event) -> Unit,
) {
    val onTitleChanged = { input: String ->
        dispatch(EditItemViewModel.Event.ChangeTitle(input))
    }
    val onUrlChanged = { input: String ->
        dispatch(EditItemViewModel.Event.ChangeUrl(input))
    }
    val onThumbnailChanged = { input: String ->
        dispatch(EditItemViewModel.Event.ChangeThumbnail(input))
    }
    val onQueryInfo = {
        dispatch(EditItemViewModel.Event.QueryInfo)
    }
    val onSubmit = {
        dispatch(EditItemViewModel.Event.Submit)
    }

    ItemForm(
        title = title,
        url = url,
        thumbnail = thumbnail,
        isLoading = isLoading,
        onTitleChanged = onTitleChanged,
        onUrlChanged = onUrlChanged,
        onThumbnailChanged = onThumbnailChanged,
        onQueryInfo = onQueryInfo,
        onSubmit = onSubmit,
    )
}
