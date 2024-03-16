package band.mlgb.ghmasta2.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.CoroutineScope

class GHMastaAppState(
    val navController: NavHostController,
    val coroutineScope: CoroutineScope
) {

    val currentBackStackEntry: State<NavBackStackEntry?>
        @Composable
        get() = navController.currentBackStackEntryAsState()


    fun navigateToSearch() {
        navController.navigate(SEARCH_ROUTE)
    }
}