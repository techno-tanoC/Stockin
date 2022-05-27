package dev.tanoc.stockin.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import dev.tanoc.stockin.App
import dev.tanoc.stockin.component.ItemListView
import dev.tanoc.stockin.model.Item
import dev.tanoc.stockin.ui.theme.StockinTheme
import dev.tanoc.stockin.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer = (application as App).appContainer
        mainViewModel = MainViewModel(appContainer.itemRepository)

        mainViewModel.load()

        setContent {
            StockinTheme {
                View()
            }
        }
    }

    @Composable
    fun View() {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("Stockin")
                    },
                )
            }
        ) {
            ItemList()
        }
    }

    @Composable
    fun ItemList() {
        val items = mainViewModel.items.collectAsState()

        val onClick = { item: Item ->
            shareUrl(item.url)
        }
        val onArchiveClick = { _: Item ->
        }
        val onEditClick = { _: Item ->
        }
        val onDeleteClick = { _: Item ->
        }

        val listState = rememberLazyListState()
        ItemListView(
            listState = listState,
            items = items,
            onClick = onClick,
            onArchiveClick = onArchiveClick,
            onEditClick = onEditClick,
            onDeleteClick = onDeleteClick,
        )
    }

    private fun shareUrl(url: String) {
        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(url)
        }
        startActivity(intent)
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        StockinTheme {
            View()
        }
    }
}
