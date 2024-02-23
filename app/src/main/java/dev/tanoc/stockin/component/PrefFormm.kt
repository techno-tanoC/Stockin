package dev.tanoc.stockin.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PrefForm(
    onSubmit: (String) -> Unit,
    onClear: () -> Unit,
) {
    var token by remember { mutableStateOf("") }
    val onTokenChanged = { input: String ->
        token = input
    }

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
            onClick = { onSubmit(token) },
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
