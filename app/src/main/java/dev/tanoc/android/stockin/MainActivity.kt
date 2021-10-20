package dev.tanoc.android.stockin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import dev.tanoc.android.stockin.model.Item
import dev.tanoc.android.stockin.ui.theme.StockinTheme
import dev.tanoc.android.stockin.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val model: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }

        model.load()
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
        val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val id = it.data?.getIntExtra("id", 0)
            val title = it.data?.getStringExtra("title")
            val url = it.data?.getStringExtra("url")

            if (id != null && title != null && url != null) {
                model.prepend(id, title, url)
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("Stockin")
                    },
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    launcher.launch(Intent(this, NewItemActivity::class.java))
                }) {
                    Icon(Icons.Rounded.Add, contentDescription = "")
                }
            }
        ) {
            ItemList()
        }
    }

    @Composable
    fun ItemList() {
        val items by model.items.observeAsState(listOf())

        LazyColumn {
            items(items) { item ->
                Item(item)
                Divider()
            }
        }
    }

    @Composable
    fun Item(item: Item) {
        Row(
            modifier = Modifier
                .clickable { shareUrl(item) }
                .fillMaxWidth(),
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
                    DropdownMenuItem(onClick = { }) {
                        Text("Archive")
                    }
                    DropdownMenuItem(onClick = { }) {
                        Text("Edit")
                    }
                    DropdownMenuItem(onClick = {
                        model.remove(item.id)
                        expanded.value = false
                    }) {
                        Text("Delete")
                    }
                }
            }
        }
    }

    private fun shareUrl(item: Item) {
        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(item.url)
        }
        startActivity(intent)
    }
}
