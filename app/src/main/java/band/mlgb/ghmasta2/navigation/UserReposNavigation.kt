package band.mlgb.ghmasta2.navigation

import androidx.navigation.NavController
import band.mlgb.ghmasta2.ui.USER_REPOS_ROUTE

internal val USER_LOGIN_PARAM_NAME = "user_login"
fun NavController.navigateToUserRepos(userLogin: String) {
    navigate("$USER_REPOS_ROUTE/$userLogin")
}