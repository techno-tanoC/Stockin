package dev.tanoc.stockin.edititem

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import dagger.hilt.android.AndroidEntryPoint
import dev.tanoc.stockin.App
import dev.tanoc.stockin.component.ItemForm
import dev.tanoc.stockin.component.StockinScaffold
import dev.tanoc.stockin.ui.theme.StockinTheme
import dev.tanoc.stockin.use
import javax.inject.Inject

@AndroidEntryPoint
class EditItemActivity : ComponentActivity() {
    @Inject
    lateinit var viewModel: RealEditItemViewModel

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
        enableEdgeToEdge()

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

    HandleEffect(
        effect = effect,
        finish = finish,
        showToast = showToast,
    )

    StockinScaffold {
        EditItemFormView(
            title = state.title,
            url = state.url,
            thumbnail = state.thumbnail,
            isLoading = state.isLoading,
            dispatch = dispatch,
        )
    }
}

@Composable
fun EditItemFormView(
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
