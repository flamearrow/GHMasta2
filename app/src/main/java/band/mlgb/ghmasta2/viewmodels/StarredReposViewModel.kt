package band.mlgb.ghmasta2.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import band.mlgb.ghmasta2.database.RepoDao
import band.mlgb.ghmasta2.ui.StarredReposUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StarredReposViewModel @Inject constructor(
    val repoDao: RepoDao
) : ViewModel() {
    val starred = repoDao.getAllStarredRepos().map {
        StarredReposUiState.Result(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = StarredReposUiState.Loading
    )

    fun unStar(repoId: Long) {
        viewModelScope.launch {
            repoDao.unstar(repoId)
        }
    }
}