package dev.tanoc.android.stockin.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ItemForm(title: String, url: String, onTitleChanged: (String) -> Unit, onUrlChanged: (String) -> Unit, onSubmit: () -> Unit) {
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
