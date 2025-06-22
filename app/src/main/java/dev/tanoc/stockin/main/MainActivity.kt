package dev.tanoc.stockin.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dagger.hilt.android.AndroidEntryPoint
import dev.tanoc.stockin.model.Item
import dev.tanoc.stockin.ui.theme.StockinTheme
import dev.tanoc.stockin.use
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var viewModel: RealMainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StockinTheme {
                MainScreen(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    vm: MainViewModel,
) {
    val (state, effect, dispatch) = use(vm)

    MainScaffold(
        settingAction = { },
        addAction = { },
    ) {
        var selected by remember { mutableStateOf<Item?>(null) }
        val scope = rememberCoroutineScope()
        val sheetState = rememberModalBottomSheetState()

        ItemModalView(
            sheetState = sheetState,
            onEditClick = { },
            onDeleteClick = { },
            onDismissRequest = { },
        )

        ItemListView(
            items = state.items,
            onClick = { item -> },
            onLongClick = { item -> },
            dispatch = dispatch,
        )
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
            HorizontalDivider()
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
            HorizontalDivider()
        }
    }

    LoadMoreHandler(
        state = listState,
        buffer = 10,
    ) {
        dispatch(MainViewModel.Event.LoadMore)
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

// https://dev.to/luismierez/infinite-lazycolumn-in-jetpack-compose-44a4
@Composable
fun LoadMoreHandler(state: LazyListState, buffer: Int, onLoadMore: () -> Unit) {
    val loadMore by remember {
        derivedStateOf {
            val layoutInfo = state.layoutInfo
            val totalCount = layoutInfo.totalItemsCount
            val lastVisibleIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1
            lastVisibleIndex > (totalCount - buffer)
        }
    }

    LaunchedEffect(loadMore) {
        snapshotFlow { loadMore }
            .distinctUntilChanged()
            .collect {
                if (it) {
                    onLoadMore()
                }
            }
    }
}
