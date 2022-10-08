package dev.tanoc.stockin.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dagger.hilt.android.AndroidEntryPoint
import dev.tanoc.stockin.App
import dev.tanoc.stockin.component.ItemView
import dev.tanoc.stockin.component.LoadMoreHandler
import dev.tanoc.stockin.component.MainScaffold
import dev.tanoc.stockin.model.Item
import dev.tanoc.stockin.ui.theme.StockinTheme
import dev.tanoc.stockin.viewmodel.MainViewModel
import dev.tanoc.stockin.viewmodel.RealMainViewModel
import dev.tanoc.stockin.viewmodel.use
import kotlinx.coroutines.launch
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
            putExtra(App.ID, item.id)
            putExtra(App.TITLE, item.title)
            putExtra(App.URL, item.url)
            putExtra(App.THUMBNAIL, item.thumbnail)
        }
        startActivity(intent)
    }
}

@OptIn(ExperimentalMaterialApi::class)
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

    val selected = remember { mutableStateOf<Item?>(null) }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = false,
    )

    val onArchiveClick = {
        scope.launch {
            sheetState.hide()
        }
        Unit
    }
    val onEditClick = {
        selected.value?.let {
            startEditItemActivity(it)
        }
        scope.launch {
            sheetState.hide()
        }
        Unit
    }
    val onDeleteClick = {
        selected.value?.let {
            dispatch(MainViewModel.Event.Delete(it.id))
        }
        scope.launch {
            sheetState.hide()
        }
        Unit
    }

    val onClick = { item: Item ->
        shareUrl(item.url)
    }
    val onLongClick = { item: Item ->
        selected.value = item
        scope.launch {
            sheetState.show()
        }
        Unit
    }

    ItemModal(
        sheetState = sheetState,
        onArchiveClick = onArchiveClick,
        onEditClick = onEditClick,
        onDeleteClick = onDeleteClick,
    ) {
        MainScaffold(
            startPrefActivity = startPrefActivity,
            startNewItemActivity = startNewItemActivity,
        ) {
            ItemList(
                items = state.items,
                isLoading = state.isLoading,
                onClick = onClick,
                onLongClick = onLongClick,
                dispatch = dispatch,
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ItemModal(
    sheetState: ModalBottomSheetState,
    onArchiveClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                ListItem(
                    icon = { Icon(Icons.Default.Check, null) },
                    text = { Text("Archive") },
                    modifier = Modifier.clickable { onArchiveClick() },
                )
                Divider()
                ListItem(
                    icon = { Icon(Icons.Default.Edit, null) },
                    text = { Text("Edit") },
                    modifier = Modifier.clickable { onEditClick() },
                )
                Divider()
                ListItem(
                    icon = { Icon(Icons.Default.Delete, null) },
                    text = { Text("Delete") },
                    modifier = Modifier.clickable { onDeleteClick() },
                )
            }
        },
        content = content,
    )
}

@Composable
fun ItemList(
    items: List<Item>,
    isLoading: Boolean,
    onClick: (Item) -> Unit,
    onLongClick: (Item) -> Unit,
    dispatch: (MainViewModel.Event) -> Unit,
) {
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
                    onLongClick,
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
