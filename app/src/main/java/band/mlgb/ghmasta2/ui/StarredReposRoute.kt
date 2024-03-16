package band.mlgb.ghmasta2.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import band.mlgb.ghmasta2.model.RepoEntity
import band.mlgb.ghmasta2.model.User
import band.mlgb.ghmasta2.navigation.navigateToUserRepos
import band.mlgb.ghmasta2.viewmodels.StarredReposViewModel

sealed interface StarredReposUiState {
    data object Loading : StarredReposUiState
    class Result(
        val starredRepos: List<RepoEntity>
    ) : StarredReposUiState
}

@Composable
fun StarredReposRoute(
    navController: NavController,
    viewModel: StarredReposViewModel = hiltViewModel()
) {
    val state by viewModel.starred.collectAsState()
    StarredReposScreen(
        state,
        onRepoStarClicked = { reporId, starred ->
            viewModel.unStar(reporId)
        },
        onUserClicked = { userClicked ->
            navController.navigateToUserRepos(userClicked)
        }
    )
}

@Composable
fun StarredReposScreen(
    state: StarredReposUiState,
    onRepoStarClicked: (Long, Boolean) -> Unit,
    onUserClicked: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {

        when (state) {
            StarredReposUiState.Loading -> {
                Loading()
            }

            is StarredReposUiState.Result -> {
                ResultView(state.starredRepos, onRepoStarClicked) { userClicked ->
                    onUserClicked(userClicked.login)
                }
            }
        }
    }
}

@Composable
fun ResultView(
    starredRepos: List<RepoEntity>,
    onRepoStarClicked: (Long, Boolean) -> Unit,
    onUserClicked: (User) -> Unit
) {
    LazyColumn {
        items(starredRepos) { repo ->
            RepoView(
                repo = repo,
                onStarClicked = { starred ->
                    onRepoStarClicked(repo.id, starred)
                },
                onUserClicked = onUserClicked
            )
        }
    }
}