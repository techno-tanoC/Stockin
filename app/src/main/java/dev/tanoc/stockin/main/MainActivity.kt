package dev.tanoc.stockin.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dagger.hilt.android.AndroidEntryPoint
import dev.tanoc.stockin.App
import dev.tanoc.stockin.edititem.EditItemActivity
import dev.tanoc.stockin.model.Item
import dev.tanoc.stockin.newitem.NewItemActivity
import dev.tanoc.stockin.token.TokenActivity
import dev.tanoc.stockin.ui.theme.StockinTheme
import dev.tanoc.stockin.use
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var viewModel: RealMainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            StockinTheme {
                MainScreen(
                    vm = viewModel,
                    shareUrl = { str -> },
                    settingAction = {
                        val intent = Intent(this@MainActivity, TokenActivity::class.java)
                        startActivity(intent)
                    },
                    addAction = {
                        val intent = Intent(this@MainActivity, NewItemActivity::class.java)
                        startActivity(intent)
                    },
                    editAction = { item ->
                        val intent = Intent(this@MainActivity, EditItemActivity::class.java).apply {
                            putExtra(App.ID, item.id)
                            putExtra(App.TITLE, item.title)
                            putExtra(App.URL, item.url)
                            putExtra(App.THUMBNAIL, item.thumbnail)
                        }
                        startActivity(intent)
                    },
                    showToast = {
                        Toast.makeText(this@MainActivity, it, Toast.LENGTH_LONG).show()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    vm: MainViewModel,
    shareUrl: (String) -> Unit,
    settingAction: () -> Unit,
    addAction: () -> Unit,
    editAction: (Item) -> Unit,
    showToast: (String) -> Unit,
) {
    val (state, effect, dispatch) = use(vm)

    HandleEffect(
        effect = effect,
        showToast = showToast,
    )

    var selected by remember { mutableStateOf<Item?>(null) }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    val onEditClick = {
        selected?.let {
            editAction(it)
        }
        scope.launch {
            sheetState.hide()
        }
        Unit
    }
    val onDeleteClick = {
        selected?.let {
            dispatch(MainViewModel.Event.Delete(it.id))
        }
        scope.launch {
            sheetState.hide()
        }
        Unit
    }
    val onDismissRequest = {
        scope.launch {
            sheetState.hide()
        }
        Unit
    }

    val onClick = { item: Item ->
        shareUrl(item.url)
    }
    val onLongClick = { item: Item ->
        selected = item
        scope.launch {
            sheetState.show()
        }
        Unit
    }

    ItemModalView(
        sheetState = sheetState,
        onEditClick = onEditClick,
        onDeleteClick = onDeleteClick,
        onDismissRequest = onDismissRequest,
    )

    MainScaffold(
        settingAction = settingAction,
        addAction = addAction,
    ) {
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = {
                dispatch(MainViewModel.Event.Reload)
            },
        ) {
            ItemListView(
                items = state.items,
                onClick = onClick,
                onLongClick = onLongClick,
                dispatch = dispatch,
            )
        }
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

    LoadMoreHandler(
        state = listState,
        buffer = 10,
    ) {
        dispatch(MainViewModel.Event.LoadMore)
    }

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
            HorizontalDivider()
        }
    }
}

@Composable
fun ItemView(
    item: Item,
    onClick: (Item) -> Unit,
    onLongClick: (Item) -> Unit,
) {
    Row(
        modifier = Modifier
            .combinedClickable(
                onClick = { onClick(item) },
                onLongClick = { onLongClick(item) },
            )
            .fillMaxWidth()
    ) {
        AsyncImage(
            model = item.thumbnail,
            contentDescription = null,
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
        )
        Text(
            text = item.title,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(4f)
                .padding(12.dp),
        )
    }
}
