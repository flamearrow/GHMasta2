package band.mlgb.ghmasta2.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import band.mlgb.ghmasta2.database.RepoDao
import band.mlgb.ghmasta2.database.UserDao
import band.mlgb.ghmasta2.domain.SearchUserRepoRemoteMediatorUsecase
import band.mlgb.ghmasta2.domain.UserSearchCase
import band.mlgb.ghmasta2.navigation.USER_LOGIN_PARAM_NAME
import band.mlgb.ghmasta2.network.Resource
import band.mlgb.ghmasta2.ui.UserReposState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserReposViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val repoDao: RepoDao,
    val userDao: UserDao,
    userSearchCase: UserSearchCase,
    userSearchUserRepoRemoteMediatorUsecase: SearchUserRepoRemoteMediatorUsecase
) : ViewModel() {
    // Use this to get params passed to this compose route
    val userId: String = requireNotNull(savedStateHandle[USER_LOGIN_PARAM_NAME])
    val userReposFlow = userSearchUserRepoRemoteMediatorUsecase(userId)

    val repoState = userSearchCase(userId).map { userResource ->
        when (userResource) {
            is Resource.Error -> UserReposState.Error(userResource.requireErrorMessage())
            is Resource.Loading -> UserReposState.Loading
            is Resource.Result -> UserReposState.Result(requireNotNull(userResource.requireData()))
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = UserReposState.Loading
    )

    fun starRepo(repoId: Long) {
        viewModelScope.launch {
            repoDao.star(repoId)
        }
    }

    fun unStarRepo(repoId: Long) {
        viewModelScope.launch {
            repoDao.unstar(repoId)
        }
    }

    fun likeUser(userId: Long) {
        viewModelScope.launch {
            userDao.like(userId)
        }
    }

    fun unlikeUser(userId: Long) {
        viewModelScope.launch {
            userDao.unlike(userId)
        }
    }

}