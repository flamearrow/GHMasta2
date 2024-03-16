package band.mlgb.ghmasta2.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import band.mlgb.ghmasta2.model.UserEntity
import band.mlgb.ghmasta2.navigation.navigateToUserRepos
import band.mlgb.ghmasta2.viewmodels.LikedUserViewModel

sealed interface LikedUserUiState {
    data object Loading : LikedUserUiState
    class Result(
        val likedUsers: List<UserEntity>
    ) : LikedUserUiState
}

@Composable
fun LikedUsersRoute(
    navController: NavController,
    viewModel: LikedUserViewModel = hiltViewModel()
) {
    val state by viewModel.starred.collectAsStateWithLifecycle()
    LikedUserScreen(
        state,
        onUserClicked = navController::navigateToUserRepos
    ) { userId ->
        viewModel.unLike(userId)
    }
}

@Composable
fun LikedUserScreen(
    state: LikedUserUiState,
    onUserClicked: (String) -> Unit,
    onUnliked: (Long) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {

        when (state) {
            LikedUserUiState.Loading -> {
                Loading()
            }

            is LikedUserUiState.Result -> {
                LazyColumn {
                    items(state.likedUsers) { user ->
                        UserView(
                            user = user,
                            onUserClicked = onUserClicked,
                        ) {
                            onUnliked(user.id)
                        }
                    }
                }
            }
        }
    }
}
