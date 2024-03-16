package band.mlgb.ghmasta2.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import band.mlgb.ghmasta2.ui.GHMastaAppState
import band.mlgb.ghmasta2.ui.LIKED_USER_ROUTE
import band.mlgb.ghmasta2.ui.LikedUsersRoute
import band.mlgb.ghmasta2.ui.SEARCH_ROUTE
import band.mlgb.ghmasta2.ui.STARRED_REPO_ROUTE
import band.mlgb.ghmasta2.ui.SearchRoute
import band.mlgb.ghmasta2.ui.StarredReposRoute
import band.mlgb.ghmasta2.ui.USER_REPOS_ROUTE
import band.mlgb.ghmasta2.ui.UserReposRoute

@Composable
fun GHNavHost(
    appState: GHMastaAppState,
    startDestination: String = STARRED_REPO_ROUTE,
    modifier: Modifier
) {
    val navController = appState.navController
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(SEARCH_ROUTE) {
            SearchRoute(navController = navController)
        }
        composable(STARRED_REPO_ROUTE) {
            StarredReposRoute(navController = navController)
        }
        composable(LIKED_USER_ROUTE) {
            LikedUsersRoute(navController)
        }
        composable(
            route = "$USER_REPOS_ROUTE/{$USER_LOGIN_PARAM_NAME}",
            arguments = listOf(
                navArgument(USER_LOGIN_PARAM_NAME) { type = NavType.StringType }
            )
        ) {
            // can also access value of USER_ID_PARAM_NAME as follows and pass it to compose view
            // val param = it.arguments?.getString(USER_ID_PARM_NAME)
            //  we chose to access it from stateHandler[USER_ID_PARM_NAME]
            UserReposRoute()
        }
    }
}