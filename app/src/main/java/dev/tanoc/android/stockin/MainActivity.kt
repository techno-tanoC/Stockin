package dev.tanoc.android.stockin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
        val openDialog = remember { mutableStateOf(false) }
        val titleField = remember { mutableStateOf("")}
        val urlField = remember { mutableStateOf("")}

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("Stockin")
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    openDialog.value = true
                }) {
                    Icon(Icons.Rounded.Add, contentDescription = "")
                }
            }
        ) {
            ItemList()
        }

        if (openDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    clear(openDialog, titleField, urlField)
                },
                title = {
                    Text(
                        text = "Input",
                        modifier = Modifier.padding(bottom = 15.dp)
                    )
                },
                text = {
                    Column {
                        TextField(
                            label = { Text("title") },
                            value = titleField.value,
                            singleLine = true,
                            onValueChange = {
                                titleField.value = it
                            },
                            modifier = Modifier
                                .padding(bottom = 15.dp)
                        )
                        TextField(
                            label = { Text("url") },
                            value = urlField.value,
                            singleLine = true,
                            onValueChange = {
                                urlField.value = it
                            }
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        model.create(titleField.value, urlField.value)
                        clear(openDialog, titleField, urlField)
                    }) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        clear(openDialog, titleField, urlField)
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }

    fun clear(open: MutableState<Boolean>, title: MutableState<String>, url: MutableState<String>) {
        open.value = false
        title.value = ""
        url.value = ""
    }

    @Composable
    fun ItemList() {
        val items by model.items.observeAsState(listOf())

        LazyColumn() {
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
                .clickable { ShareUrl(item) }
                .fillMaxWidth()
        ) {
            Text(
                text = item.title,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(3.dp)
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
    }

    fun ShowToast(message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }

    fun ShareUrl(item: Item) {
        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(item.url)
        }
        startActivity(intent)
    }
}
