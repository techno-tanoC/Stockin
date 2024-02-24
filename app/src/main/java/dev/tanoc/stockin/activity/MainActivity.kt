package dev.tanoc.stockin.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import dev.tanoc.stockin.App
import dev.tanoc.stockin.component.ItemView
import dev.tanoc.stockin.component.LoadMoreHandler
import dev.tanoc.stockin.component.MainScaffold
import dev.tanoc.stockin.component.PullRefreshBox
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
                    shareUrl = {
                        shareUrl(it)
                    },
                    settingAction = {
                        val intent = Intent(this@MainActivity, TokenActivity::class.java)
                        startActivity(intent)
                    },
                    addAction = {
                        val intent = Intent(this@MainActivity, NewItemActivity::class.java)
                        startActivity(intent)
                    },
                    editAction = {
                        startEditItemActivity(it)
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

    LaunchedEffect(effect) {
        effect.collect { effect ->
            when (effect) {
                is MainViewModel.Effect.ShowToast -> {
                    showToast(effect.message)
                }
            }
        }
    }

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
        PullRefreshBox(
            refreshing = state.isLoading,
            onRefresh = { dispatch(MainViewModel.Event.Reload) }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemModalView(
    sheetState: SheetState,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    if (sheetState.isVisible) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = onDismissRequest,
        ) {
            ListItem(
                leadingContent = { Icon(Icons.Default.Edit, null) },
                headlineContent = { Text("Edit") },
                modifier = Modifier.clickable { onEditClick() },
            )
            Divider()
            ListItem(
                leadingContent = { Icon(Icons.Default.Delete, null) },
                headlineContent = { Text("Delete") },
                modifier = Modifier.clickable { onDeleteClick() },
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
