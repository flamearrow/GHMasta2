package band.mlgb.ghmasta2.navigation

import androidx.navigation.NavBackStackEntry
import band.mlgb.ghmasta2.ui.DEFAULT_TITLE
import band.mlgb.ghmasta2.ui.LIKED_USER_ROUTE
import band.mlgb.ghmasta2.ui.SEARCH_ROUTE
import band.mlgb.ghmasta2.ui.STARRED_REPO_ROUTE
import band.mlgb.ghmasta2.ui.USER_REPOS_ROUTE

fun NavBackStackEntry?.isSearch() = this?.destination?.route == SEARCH_ROUTE
fun NavBackStackEntry?.isStarredRepos() = this?.destination?.route == STARRED_REPO_ROUTE
fun NavBackStackEntry?.isLikedUsers() = this?.destination?.route == LIKED_USER_ROUTE
fun NavBackStackEntry?.isUserRepos() = this?.destination?.route == USER_REPOS_ROUTE
fun NavBackStackEntry?.shouldShowTopBar() = this?.destination?.route != SEARCH_ROUTE

fun NavBackStackEntry?.maybeGetTitleOverride() = this?.destination?.route?.let { route ->
    if (route.startsWith(USER_REPOS_ROUTE)) {
        this.arguments?.getString(USER_LOGIN_PARAM_NAME) ?: DEFAULT_TITLE
    } else {
        DEFAULT_TITLE
    }
} ?: DEFAULT_TITLE

fun NavBackStackEntry?.shouldShowBottomBar() =
    this?.isStarredRepos() == true || this?.isLikedUsers() == true