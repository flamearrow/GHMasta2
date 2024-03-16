package band.mlgb.ghmasta2.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import band.mlgb.ghmasta2.R
import band.mlgb.ghmasta2.model.RepoEntity
import band.mlgb.ghmasta2.model.User
import band.mlgb.ghmasta2.network.openUrl
import band.mlgb.ghmasta2.ui.theme.GHMasta2Theme
import coil.compose.AsyncImage

@Composable
fun RepoView(
    repo: RepoEntity,
    onStarClicked: (Boolean) -> Unit,
    onUserClicked: (User) -> Unit
) {
    var starred by remember(repo) {
        mutableStateOf(repo.starred)
    }
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .padding(5.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f)
                    .padding(horizontal = 5.dp)
                    .clickable {
                        context.openUrl(repo.html_url)
                    }
            ) {
                Text(
                    text = repo.name,
                    style = MaterialTheme.typography.titleMedium
                )
                repo.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 5.dp)
            ) {
                repo.owner.avatar_url?.let {
                    if (LocalInspectionMode.current) {
                        Image(
                            painter = painterResource(id = R.drawable.tora),
                            modifier = Modifier.size(50.dp),
                            contentDescription = "tora place holder"
                        )
                    } else {
                        AsyncImage(
                            model = it,
                            modifier = Modifier
                                .size(50.dp)
                                .clickable {
                                    onUserClicked(repo.owner)
                                },
                            contentDescription = "avatar of user ${repo.owner.login}",
                        )
                    }
                }
                Text(text = repo.owner.login)
            }

            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(5.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    modifier = Modifier
                        .width(20.dp)
                        .clickable {
                            onStarClicked(!repo.starred)
                            starred = !starred
                        },
                    painter = if (starred)
                        painterResource(id = R.drawable.star_fill)
                    else
                        painterResource(id = R.drawable.star_no_fill),
                    contentDescription = "starred"
                )
            }
        }
    }
}

@Preview
@Composable
fun RepoViewPreview() {
    GHMasta2Theme(
        dynamicColor = false
    ) {
        Surface {
            RepoView(
                repo = RepoEntity(
                    id = 1,
                    name = "repo1",
                    description = "Repo1's description",
                    owner = User(
                        user_id = 123,
                        login = "flamearrow",
                        avatar_url = "https://avatars.githubusercontent.com/u/4720570?v=4",
                        user_html_url = "https://blah"
                    ),
                    starred = true,
                    html_url = "https://bleh"
                ),
                {},
                {}
            )
        }
    }
}