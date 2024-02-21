package dev.tanoc.stockin.component

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import eu.bambooapps.material3.pullrefresh.PullRefreshIndicator
import eu.bambooapps.material3.pullrefresh.pullRefresh
import eu.bambooapps.material3.pullrefresh.rememberPullRefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullRefreshBox(
    refreshing: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit,
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = onRefresh,
    )

    Box(
        modifier = Modifier.pullRefresh(pullRefreshState),
    ) {
        content()

        PullRefreshIndicator(
            refreshing = refreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}
