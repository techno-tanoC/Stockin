package dev.tanoc.stockin.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    settingAction: () -> Unit,
    addAction: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Stockin")
                },
                actions = {
                    IconButton(onClick = settingAction) {
                        Icon(Icons.Rounded.Settings, "")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = addAction) {
                Icon(Icons.Rounded.Add, contentDescription = "")
            }
        }
    ) {
        Column(
            modifier = Modifier.padding(it),
            content = content,
        )
    }
}
