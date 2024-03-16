package band.mlgb.ghmasta2.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import band.mlgb.ghmasta2.database.KeywordReposPageDao
import band.mlgb.ghmasta2.database.RepoDao
import band.mlgb.ghmasta2.database.UserDao
import band.mlgb.ghmasta2.database.UserReposPageDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    val userDao: UserDao,
    val repoDao: RepoDao,
    val userReposPageDao: UserReposPageDao,
    val keywordReposPageDao: KeywordReposPageDao
) : ViewModel() {
    fun clearUserDB() {
        viewModelScope.launch {
            userDao.clear()
        }
    }

    fun clearRepoDB() {
        viewModelScope.launch {
            repoDao.clear()
        }
    }

    fun clearMediatorPageCaches() {
        viewModelScope.launch {
            userReposPageDao.clear()
            keywordReposPageDao.clear()
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            userDao.clear()
            repoDao.clear()
            userReposPageDao.clear()
            keywordReposPageDao.clear()
        }
    }
}