package dev.tanoc.android.stockin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dev.tanoc.android.stockin.model.EventObserver
import dev.tanoc.android.stockin.model.Item
import dev.tanoc.android.stockin.ui.theme.StockinTheme
import dev.tanoc.android.stockin.viewmodel.MainViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.collect

class MainActivity : ComponentActivity() {
    private val model: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }

    private fun prepend(result: ActivityResult) {
        val id = result.data?.getLongExtra("id", 0)
        val title = result.data?.getStringExtra("title")
        val url = result.data?.getStringExtra("url")

        if (id != null && title != null && url != null) {
            model.prepend(id, title, url)
        }
    }

    private fun patch(result: ActivityResult) {
        val id = result.data?.getLongExtra("id", 0)
        val title = result.data?.getStringExtra("title")
        val url = result.data?.getStringExtra("url")

        if (id != null && title != null && url != null) {
            model.patch(id, title, url)
        }
    }

    private fun shareUrl(item: Item) {
        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(item.url)
        }
        startActivity(intent)
    }

    @Preview(showBackground = true)
    @Composable
    fun App() {
        StockinTheme {
            Container()
        }
    }

    @Composable
    fun Container() {
        model.message.observe(this, EventObserver {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        })

        val newLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            prepend(it)
        }

        val isLoading by model.isLoading.observeAsState(false)

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("Stockin")
                    },
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    newLauncher.launch(Intent(this@MainActivity, NewItemActivity::class.java))
                }) {
                    Icon(Icons.Rounded.Add, contentDescription = "")
                }
            }
        ) {
            SwipeRefresh(
                state = rememberSwipeRefreshState(isLoading),
                onRefresh = { model.reload() },
            ) {
                ItemList()
            }
        }
    }

    @Composable
    fun ItemList() {
        val editLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            patch(it)
        }

        val onClick = { item: Item ->
            shareUrl(item)
        }
        val onArchiveClick = { _: Item ->
        }
        val onEditClick = { item: Item ->
            val intent = Intent(this@MainActivity, EditItemActivity::class.java).apply {
                putExtra("id", item.id)
                putExtra("title", item.title)
                putExtra("url", item.url)
            }
            editLauncher.launch(intent)
        }
        val onDeleteClick = { item: Item ->
            model.remove(item.id)
        }

        val listState = rememberLazyListState()
        ListHandler(listState, buffer = 25) {
            model.loadMore()
        }

        val items by model.items.observeAsState(listOf())
        LazyColumn(
            state = listState,
        ) {
            items(items) { item ->
                Item(item, onClick, onArchiveClick, onEditClick, onDeleteClick)
                Divider()
            }
        }
    }

    @Composable
    fun ListHandler(state: LazyListState, buffer: Int, onLoadMore: () -> Unit) {
        val loadMore = remember {
            derivedStateOf {
                val layoutInfo = state.layoutInfo
                val totalCount = layoutInfo.totalItemsCount
                val lastVisibleIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1
                lastVisibleIndex > (totalCount - buffer)
            }
        }
        LaunchedEffect(loadMore) {
            snapshotFlow { loadMore.value }
                .distinctUntilChanged()
                .collect {
                    if (loadMore.value) {
                        onLoadMore()
                    }
                }
        }
    }

    @Composable
    fun Item(item: Item, onClick: (Item) -> Unit, onArchiveClick: (Item) -> Unit, onEditClick: (Item) -> Unit, onDeleteClick: (Item) -> Unit) {
        Row(
            modifier = Modifier
                .clickable { onClick(item) }
                .fillMaxWidth(),
        ) {
            Text(
                text = item.title,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp),
            )
            Box {
                val expanded = remember { mutableStateOf(false) }

                IconButton(
                    onClick = { expanded.value = true },
                ) {
                    Icon(Icons.Rounded.MoreVert, contentDescription = "")
                }
                DropdownMenu(
                    expanded = expanded.value,
                    onDismissRequest = { expanded.value = false },
                    offset = DpOffset(8.dp, 0.dp),
                ) {
                    DropdownMenuItem(onClick = {
                        onArchiveClick(item)
                        expanded.value = false
                    }) {
                        Text("Archive")
                    }
                    DropdownMenuItem(onClick = {
                        onEditClick(item)
                        expanded.value = false
                    }) {
                        Text("Edit")
                    }
                    DropdownMenuItem(onClick = {
                        onDeleteClick(item)
                        expanded.value = false
                    }) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}
