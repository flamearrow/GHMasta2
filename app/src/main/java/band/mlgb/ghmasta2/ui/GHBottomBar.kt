package band.mlgb.ghmasta2.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import band.mlgb.ghmasta2.R
import band.mlgb.ghmasta2.navigation.isLikedUsers
import band.mlgb.ghmasta2.navigation.isStarredRepos

@Composable
fun GHBottomBar(
    navController: NavController, currentBackStackEntry: NavBackStackEntry?
) {
    NavigationBar {
        NavigationBarItem(
            label = {
                Text("Starred Repos")
            },
            selected = currentBackStackEntry.isStarredRepos(),
            onClick = {
                navController.navigate(STARRED_REPO_ROUTE)
            },
            icon = {
                if (currentBackStackEntry.isStarredRepos()) {
                    Icon(
                        painter = painterResource(id = R.drawable.star_fill),
                        contentDescription = "starred repos"
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.star_no_fill),
                        contentDescription = "starred repos"
                    )
                }
            }
        )

        NavigationBarItem(
            label = {
                Text("Liked Users")
            },
            selected = currentBackStackEntry.isLikedUsers(),
            onClick = { navController.navigate(LIKED_USER_ROUTE) },
            icon = {
                if (currentBackStackEntry.isLikedUsers()) {
                    Icon(
                        painter = painterResource(id = R.drawable.thumb_up_fill),
                        contentDescription = "liked users"
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.thumb_up_no_fill),
                        contentDescription = "liked users"
                    )
                }
            })
    }
}