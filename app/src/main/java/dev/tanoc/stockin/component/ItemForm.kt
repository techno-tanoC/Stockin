package dev.tanoc.stockin.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ItemForm(
    title: String,
    url: String,
    thumbnail: String,
    isLoading: Boolean,
    onTitleChanged: (String) -> Unit,
    onUrlChanged: (String) -> Unit,
    onThumbnailChanged: (String) -> Unit,
    onQueryInfo: () -> Unit,
    onSubmit: () -> Unit,
) {
    Box {
        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(8.dp),
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChanged,
                label = { Text("Title") },
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
            )
            OutlinedTextField(
                value = url,
                onValueChange = onUrlChanged,
                label = { Text("Url") },
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
            )
            OutlinedTextField(
                value = thumbnail,
                onValueChange = onThumbnailChanged,
                label = { Text("Thumbnail") },
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
            )
            Button(
                onClick = onQueryInfo,
                modifier = Modifier
                    .padding(4.dp, 8.dp)
                    .fillMaxWidth(),
            ) {
                Text(
                    text = "Query info",
                    modifier = Modifier.padding(12.dp),
                )
            }
            Button(
                onClick = onSubmit,
                modifier = Modifier
                    .padding(4.dp, 8.dp)
                    .fillMaxWidth(),
            ) {
                Text(
                    text = "Submit",
                    modifier = Modifier.padding(12.dp),
                )
            }
        }
    }
}
