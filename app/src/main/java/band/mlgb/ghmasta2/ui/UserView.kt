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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import band.mlgb.ghmasta2.R
import band.mlgb.ghmasta2.model.UserEntity
import band.mlgb.ghmasta2.ui.theme.GHMasta2Theme
import coil.compose.AsyncImage

@Composable
fun UserView(
    user: UserEntity,
    onUserClicked: (String) -> Unit = {},
    onLikeClicked: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(25.dp),
        shape = MaterialTheme.shapes.extraLarge,
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.onPrimary),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column {
                user.avatar_url?.let {
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
                                    onUserClicked(user.login)
                                },
                            contentDescription = "avatar of user ${user.login}",
                        )
                    }
                }
                Text(text = user.login)
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
                            onLikeClicked(!user.liked)
                        },
                    painter = if (user.liked)
                        painterResource(id = R.drawable.thumb_up_fill)
                    else
                        painterResource(id = R.drawable.thumb_up_no_fill),
                    contentDescription = "starred"
                )

            }
        }
    }
}

@Preview
@Composable
fun UserViewPreview() {
    GHMasta2Theme(
        dynamicColor = false
    ) {
        Surface {
            UserView(
                user = UserEntity(
                    id = 123,
                    login = "mlgb",
                    avatar_url = "someUrl",
                    liked = false,
                    html_url = "https://bleh"
                ),
                {},
                {}
            )
        }
    }
}