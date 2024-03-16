package band.mlgb.ghmasta2.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import band.mlgb.ghmasta2.navigation.GHNavHost
import band.mlgb.ghmasta2.navigation.maybeGetTitleOverride
import band.mlgb.ghmasta2.navigation.shouldShowBottomBar
import band.mlgb.ghmasta2.navigation.shouldShowTopBar
import kotlinx.coroutines.CoroutineScope

@Composable
fun rememberGHMastaAppState(
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): GHMastaAppState {
    return remember(
        navController,
        coroutineScope
    ) {
        GHMastaAppState(
            navController = navController,
            coroutineScope = coroutineScope
        )
    }
}

@Composable
fun GHMasta2App(
    appState: GHMastaAppState = rememberGHMastaAppState()
) {
    val currentBackStackEntry by appState.currentBackStackEntry
    var showSettings by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            if (currentBackStackEntry.shouldShowTopBar()) {
                GHTopBar(
                    title = currentBackStackEntry.maybeGetTitleOverride(),
                    onNavigationClick = {
                        appState.navigateToSearch()
                    },
                    onSettingsClick = {
                        showSettings = true
                    }
                )
            }
        },
        bottomBar = {
            if (currentBackStackEntry.shouldShowBottomBar()) {
                GHBottomBar(
                    navController = appState.navController,
                    currentBackStackEntry = currentBackStackEntry
                )
            }
        },
    ) { padding ->

        if (showSettings) {
            SettingsDialog {
                showSettings = false
            }
        }

        GHNavHost(appState = appState, modifier = Modifier.padding(padding))
    }
}


internal val DEFAULT_TITLE = "GHMasta"
internal val SEARCH_ROUTE = "searchRoute"
internal val STARRED_REPO_ROUTE = "starredRepoRoute"
internal val LIKED_USER_ROUTE = "likeUserRoute"
internal val USER_REPOS_ROUTE = "userReposRoute"