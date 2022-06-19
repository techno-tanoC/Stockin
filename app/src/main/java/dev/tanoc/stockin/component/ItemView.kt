package dev.tanoc.stockin.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.tanoc.stockin.model.Item

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemView(
    item: Item,
    onClick: (Item) -> Unit,
    onArchiveClick: (Item) -> Unit,
    onEditClick: (Item) -> Unit,
    onDeleteClick: (Item) -> Unit,
) {
    val expanded = remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .combinedClickable(
                onClick = { onClick(item) },
                onLongClick = { expanded.value = true },
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
        Box {
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
