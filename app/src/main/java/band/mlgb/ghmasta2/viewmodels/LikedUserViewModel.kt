package band.mlgb.ghmasta2.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import band.mlgb.ghmasta2.database.UserDao
import band.mlgb.ghmasta2.ui.LikedUserUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LikedUserViewModel @Inject constructor(
    val userDao: UserDao
) : ViewModel() {

    val starred = userDao.getAllLikedUsers().map {
        LikedUserUiState.Result(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = LikedUserUiState.Loading
    )

    fun unLike(userId: Long) {
        viewModelScope.launch {
            userDao.unlike(userId)
        }
    }
}