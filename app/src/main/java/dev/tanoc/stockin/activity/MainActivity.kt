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
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dev.tanoc.stockin.App
import dev.tanoc.stockin.component.ItemView
import dev.tanoc.stockin.model.Item
import dev.tanoc.stockin.ui.theme.StockinTheme
import dev.tanoc.stockin.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer = (application as App).appContainer
        mainViewModel = MainViewModel(appContainer.itemRepository, appContainer.prefRepository)

        mainViewModel.load()

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
                    actions = {
                        IconButton(onClick = {
                            val intent = Intent(this@MainActivity, PrefActivity::class.java)
                            startActivity(intent)
                        }) {
                            Icon(Icons.Rounded.Settings, "")
                        }
                    },
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        val intent = Intent(this@MainActivity, NewItemActivity::class.java)
                        startActivity(intent)
                    }
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = "")
                }
            },
        ) {
            ItemList()
        }
    }

    @Composable
    fun ItemList() {
        val items = mainViewModel.items.collectAsState()
        val isLoading = mainViewModel.isLoading.collectAsState()

        val onClick = { item: Item ->
            shareUrl(item.url)
        }
        val onArchiveClick = { _: Item ->
        }
        val onEditClick = { _: Item ->
        }
        val onDeleteClick = { _: Item ->
        }

        val listState = rememberLazyListState()
        val swipeRefreshState = rememberSwipeRefreshState(isLoading.value)

        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { mainViewModel.load() }
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                items(items.value) { item ->
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
    }

    private fun shareUrl(url: String) {
        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(url)
        }
        startActivity(intent)
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        StockinTheme {
            View()
        }
    }
}
