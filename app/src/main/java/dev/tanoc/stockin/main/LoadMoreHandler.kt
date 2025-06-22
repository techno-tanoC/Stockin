package dev.tanoc.stockin.main

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged

// https://dev.to/luismierez/infinite-lazycolumn-in-jetpack-compose-44a4
@Composable
fun LoadMoreHandler(
    state: LazyListState,
    buffer: Int,
    onLoadMore: () -> Unit,
) {
    val loadMore by remember {
        derivedStateOf {
            val layoutInfo = state.layoutInfo
            val totalCount = layoutInfo.totalItemsCount
            val lastVisibleIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1
            lastVisibleIndex > (totalCount - buffer)
        }
    }

    LaunchedEffect(loadMore) {
        snapshotFlow { loadMore }
            .distinctUntilChanged()
            .collect {
                if (it) {
                    onLoadMore()
                }
            }
    }
}
