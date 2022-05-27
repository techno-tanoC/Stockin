package dev.tanoc.stockin.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import dev.tanoc.stockin.model.Item

@Composable
fun ItemListView(
    listState: LazyListState,
    items: State<List<Item>>,
    onClick: (Item) -> Unit,
    onArchiveClick: (Item) -> Unit,
    onEditClick: (Item) -> Unit,
    onDeleteClick: (Item) -> Unit,
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
