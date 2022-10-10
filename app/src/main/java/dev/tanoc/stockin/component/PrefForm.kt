package dev.tanoc.stockin.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PrefForm(
    token: String,
    onTokenChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onClear: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(8.dp),
    ) {
        OutlinedTextField(
            value = token,
            onValueChange = onTokenChanged,
            label = { Text("Token") },
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
                modifier = Modifier
                    .padding(12.dp)
            )
        }
        Button(
            onClick = onClear,
            modifier = Modifier
                .padding(4.dp, 8.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = "Clear",
                modifier = Modifier
                    .padding(12.dp)
            )
        }
    }
}
