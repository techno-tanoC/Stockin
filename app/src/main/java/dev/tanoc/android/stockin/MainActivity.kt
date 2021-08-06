package dev.tanoc.android.stockin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.tanoc.android.stockin.ui.theme.StockinTheme

data class Item(val url: String, val title: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun App() {
        StockinTheme {
            Container()
        }
    }

    @Composable
    fun Container() {
        val items = remember {
            mutableStateListOf<Item>(
                Item(
                    "https://example.com/",
                    "example"
                ),
                Item(
                    "https://example.com/",
                    "exampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexampleexample"
                )
            )
        }
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("Stockin")
                    }
                )
            },
            content = {
                ItemList(items = items)
            }
        )
    }

    @Composable
    fun ItemList(items: SnapshotStateList<Item>) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item.title,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .padding(3.dp)
                            .clickable { ShareUrl(item) }
                            .padding(10.dp)
                    )
                    Box() {
                        val expanded = remember { mutableStateOf(false) }

                        IconButton(
                            onClick = { expanded.value = true }
                        ) {
                            Icon(Icons.Rounded.MoreVert, contentDescription = "")
                        }
                        DropdownMenu(
                            expanded = expanded.value,
                            onDismissRequest = { expanded.value = false }
                        ) {
                            DropdownMenuItem(onClick = { }) {
                                Text("Archive")
                            }
                            DropdownMenuItem(onClick = { }) {
                                Text("Edit")
                            }
                            DropdownMenuItem(onClick = { }) {
                                Text("Delete")
                            }
                        }
                    }
                }
                Divider()
            }
        }
    }

    fun ShowToast(message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }

    fun ShareUrl(item: Item) {
        val share = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, item.url)
            type = "text/plain"
        }
        startActivity(share)
    }
}
