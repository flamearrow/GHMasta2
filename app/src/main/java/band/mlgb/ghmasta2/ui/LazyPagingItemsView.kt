package band.mlgb.ghmasta2.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import band.mlgb.ghmasta2.model.RepoEntity
import band.mlgb.ghmasta2.model.User

@Composable
fun LazyPagingItemsView(
    lazyPagingItems: LazyPagingItems<RepoEntity>,
    onRepoStarClicked: (RepoEntity, Boolean) -> Unit,
    onUserClicked: (User) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        when (val refreshState = lazyPagingItems.loadState.refresh) {
            LoadState.Loading -> {
                Loading() // full screen loading
            }

            is LoadState.Error -> {
                Error(refreshState.error.message) // full screen error
            }
            //            lazyPagingItems.itemCount == 0 -> {} // don't need to handle this specifically
            else -> {
                LazyColumn {
                    // Always display the result when available
                    items(count = lazyPagingItems.itemCount) {
                        lazyPagingItems[it]?.let { repoEntity ->
                            RepoView(
                                repo = repoEntity,
                                onStarClicked = { starred ->
                                    onRepoStarClicked(repoEntity, starred)
                                },
                                onUserClicked = onUserClicked
                            )
                        }
                    }

                    // This will be appended to the result when getting more data
                    if (lazyPagingItems.loadState.append == LoadState.Loading) {
                        item {
                            Loading() // line loading
                        }
                    }
                }
            }
        }
    }
}

fun LazyPagingItems<RepoEntity>.isSearching(): Boolean {
    return loadState.refresh == LoadState.Loading
}

fun LazyPagingItems<RepoEntity>.noResults(): Boolean {
    loadState.refresh.let { refreshState ->
        return refreshState != LoadState.Loading && refreshState !is LoadState.Error && itemCount == 0
    }
}