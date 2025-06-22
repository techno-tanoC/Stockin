package dev.tanoc.stockin.main

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

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
