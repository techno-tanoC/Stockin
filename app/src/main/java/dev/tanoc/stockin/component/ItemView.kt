package dev.tanoc.stockin.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.tanoc.stockin.model.Item

@OptIn(ExperimentalFoundationApi::class)
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
