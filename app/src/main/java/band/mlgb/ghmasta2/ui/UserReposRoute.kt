package band.mlgb.ghmasta2.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import band.mlgb.ghmasta2.model.RepoEntity
import band.mlgb.ghmasta2.model.UserEntity
import band.mlgb.ghmasta2.network.openUrl
import band.mlgb.ghmasta2.viewmodels.UserReposViewModel


sealed interface UserReposState {
    data object Loading : UserReposState
    class Error(val message: String) : UserReposState

    class Result(
        val user: UserEntity
    ) : UserReposState
}

@Composable
fun UserReposRoute(
    viewModel: UserReposViewModel = hiltViewModel()
) {
    val repoState by viewModel.repoState.collectAsStateWithLifecycle()
    val lazyPagingItems = viewModel.userReposFlow.collectAsLazyPagingItems()
    UserReposView(
        reposState = repoState,
        lazyRepoPagingItems = lazyPagingItems,
        onUserLikeClicked = { userId, liked ->
            if (liked) {
                viewModel.likeUser(userId)
            } else {
                viewModel.unlikeUser(userId)
            }
        },
        onRepoStarClicked = { repoId, starred ->
            if (starred) {
                viewModel.starRepo(repoId)
            } else {
                viewModel.unStarRepo(repoId)
            }
        }
    )
}

@Composable
fun UserReposView(
    reposState: UserReposState,
    lazyRepoPagingItems: LazyPagingItems<RepoEntity>,
    onUserLikeClicked: (Long, Boolean) -> Unit,
    onRepoStarClicked: (Long, Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        when (reposState) {
            UserReposState.Loading -> Loading()
            is UserReposState.Error -> Error("MLG")
            is UserReposState.Result -> ResultView(
                reposState.user,
                lazyRepoPagingItems,
                onUserLikeClicked = onUserLikeClicked,
                onRepoStarClicked = onRepoStarClicked
            )
        }
    }
}

@Composable
fun ResultView(
    user: UserEntity,
    lazyRepoPagingItems: LazyPagingItems<RepoEntity>,
    onUserLikeClicked: (Long, Boolean) -> Unit,
    onRepoStarClicked: (Long, Boolean) -> Unit
) {
    val context = LocalContext.current
    // Don't navigate when user is clicked as we're already on the user repoview
    UserView(
        user = user,
        onUserClicked = { context.openUrl(user.html_url) }
    ) { liked ->
        onUserLikeClicked(user.id, liked)
    }
    LazyPagingItemsView(
        lazyPagingItems = lazyRepoPagingItems,
        onRepoStarClicked = { repoEntity, starred ->
            onRepoStarClicked(repoEntity.id, starred)
        },
        onUserClicked = {
            context.openUrl(it.user_html_url)
        }
    )
}