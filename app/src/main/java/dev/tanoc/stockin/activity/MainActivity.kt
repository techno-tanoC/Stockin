package dev.tanoc.stockin.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import dev.tanoc.stockin.component.ItemView
import dev.tanoc.stockin.component.LoadMoreHandler
import dev.tanoc.stockin.component.MainScaffold
import dev.tanoc.stockin.model.Item
import dev.tanoc.stockin.ui.theme.StockinTheme
import dev.tanoc.stockin.viewmodel.MainViewModel
import dev.tanoc.stockin.viewmodel.RealMainViewModel
import dev.tanoc.stockin.viewmodel.use
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var viewModel: RealMainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StockinTheme {
                MainScreen(
                    vm = viewModel,
                    shareUrl = {
                        shareUrl(it)
                    },
                )
            }
        }
    }

    private fun shareUrl(url: String) {
        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(url)
        }
        startActivity(intent)
    }
}

@Composable
fun MainScreen(
    vm: MainViewModel,
    shareUrl: (String) -> Unit,
) {
    val (state, effect, dispatch) = use(vm)

    val onClick = { item: Item ->
        shareUrl(item.url)
    }
    val onLongClick = { item: Item ->
    }

    MainScaffold(
        settingAction = {},
        addAction = {},
    ) {
        ItemListView(
            items = state.items,
            onClick = onClick,
            onLongClick = onLongClick,
            dispatch = dispatch,
        )
    }
}

@Composable
fun ItemListView(
    items: List<Item>,
    onClick: (Item) -> Unit,
    onLongClick: (Item) -> Unit,
    dispatch: (MainViewModel.Event) -> Unit,
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize(),
    ) {
        items(items) { item ->
            ItemView(
                item,
                onClick,
                onLongClick,
            )
            Divider()
        }
    }

    LoadMoreHandler(
        state = listState,
        buffer = 10,
    ) {
        dispatch(MainViewModel.Event.LoadMore)
    }
}
