package dev.tanoc.stockin.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun StockinScaffold(
    content: @Composable ColumnScope.() -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Stockin")
                },
            )
        },
    ) {
        Column(
            modifier = Modifier
                .padding(it),
            content = content,
        )
    }
}

@Composable
fun MainScaffold(
    startPrefActivity: () -> Unit,
    startNewItemActivity: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Stockin")
                },
                actions = {
                    IconButton(onClick = startPrefActivity) {
                        Icon(Icons.Rounded.Settings, "")
                    }
                }
            )
        },
        floatingActionButton = {
        FloatingActionButton(onClick = startNewItemActivity) {
            Icon(Icons.Rounded.Add, contentDescription = "")
        }
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it),
            content = content,
        )
    }
}
