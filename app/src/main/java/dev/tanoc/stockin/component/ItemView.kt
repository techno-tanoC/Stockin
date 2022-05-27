package dev.tanoc.stockin.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import dev.tanoc.stockin.model.Item

@Composable
fun ItemView(
    item: Item,
    onClick: (Item) -> Unit,
    onArchiveClick: (Item) -> Unit,
    onEditClick: (Item) -> Unit,
    onDeleteClick: (Item) -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable { onClick(item) }
            .fillMaxWidth()
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
