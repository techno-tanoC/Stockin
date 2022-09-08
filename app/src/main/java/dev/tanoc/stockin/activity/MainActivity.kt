package dev.tanoc.stockin.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dev.tanoc.stockin.App
import dev.tanoc.stockin.component.ItemView
import dev.tanoc.stockin.component.LoadMoreHandler
import dev.tanoc.stockin.model.Item
import dev.tanoc.stockin.ui.theme.StockinTheme
import dev.tanoc.stockin.viewmodel.MainViewModel
import dev.tanoc.stockin.viewmodel.MainViewModelFactory
import dev.tanoc.stockin.viewmodel.RealMainViewModel
import dev.tanoc.stockin.viewmodel.use

class MainActivity : ComponentActivity() {
    private val viewModel by lazy {
        val appContainer = (application as App).appContainer
        val factory = MainViewModelFactory(
            appContainer.itemRepository,
            appContainer.prefRepository,
        )
        ViewModelProvider(this, factory).get(RealMainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            StockinTheme {
                MainScreen(
                    vm = viewModel,
                    startPrefActivity = {
                        val intent = Intent(this@MainActivity, PrefActivity::class.java)
                        startActivity(intent)
                    },
                    startNewItemActivity = {
                        val intent = Intent(this@MainActivity, NewItemActivity::class.java)
                        startActivity(intent)
                    },
                    startEditItemActivity = {
                        startEditItemActivity(it)
                    },
                    shareUrl = {
                        shareUrl(it)
                    },
                    showToast = {
                        Toast.makeText(this@MainActivity, it, Toast.LENGTH_LONG).show()
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

    private fun startEditItemActivity(item: Item) {
        val intent = Intent(this@MainActivity, EditItemActivity::class.java).apply {
            putExtra("id", item.id)
            putExtra("title", item.title)
            putExtra("url", item.url)
            putExtra("thumbnail", item.thumbnail)
        }
        startActivity(intent)
    }
}

@Composable
fun MainScreen(
    vm: MainViewModel,
    startPrefActivity: () -> Unit,
    startNewItemActivity: () -> Unit,
    startEditItemActivity: (Item) -> Unit,
    shareUrl: (String) -> Unit,
    showToast: (String) -> Unit,
) {
    val (state, effect, dispatch) = use(vm)

    LaunchedEffect(effect) {
        effect.collect { effect ->
            when (effect) {
                is MainViewModel.Effect.ShowToast -> {
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
                actions = {
                    IconButton(onClick = startPrefActivity) {
                        Icon(Icons.Rounded.Settings, "")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = startNewItemActivity) {
                Icon(Icons.Rounded.Add, contentDescription = "")
            }
        },
    ) {
        Column(
            modifier = Modifier
                .padding(it)
        ) {
            ItemList(
                items = state.items,
                isLoading = state.isLoading,
                dispatch = dispatch,
                startEditItemActivity = startEditItemActivity,
                shareUrl = shareUrl,
            )
        }
    }
}

@Composable
fun ItemList(
    items: List<Item>,
    isLoading: Boolean,
    dispatch: (MainViewModel.Event) -> Unit,
    startEditItemActivity: (Item) -> Unit,
    shareUrl: (String) -> Unit,
) {
    val onClick = { item: Item ->
        shareUrl(item.url)
    }
    val onArchiveClick = { _: Item ->
    }
    val onEditClick = { item: Item ->
        startEditItemActivity(item)
    }
    val onDeleteClick = { item: Item ->
        dispatch(MainViewModel.Event.Delete(item.id))
    }

    val listState = rememberLazyListState()
    val swipeRefreshState = rememberSwipeRefreshState(isLoading)

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = { dispatch(MainViewModel.Event.Reload) }
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize(),
        ) {
            items(items) { item ->
                ItemView(
                    item,
                    onClick,
                    onArchiveClick,
                    onEditClick,
                    onDeleteClick,
                )
                Divider()
            }
        }
    }

    LoadMoreHandler(
        state = listState,
        buffer = 25
    ) {
        dispatch(MainViewModel.Event.LoadMore)
    }
}
