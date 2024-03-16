package band.mlgb.ghmasta2.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import band.mlgb.ghmasta2.viewmodels.SettingsViewModel

@Composable
fun SettingsDialog(
    viewModel: SettingsViewModel = hiltViewModel(),
    onDismissRequest: () -> Unit
) {
    val configuration = LocalConfiguration.current
    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 80.dp),
        onDismissRequest = onDismissRequest,
        confirmButton = {},
        title = {
            Text(text = "Debug options", style = MaterialTheme.typography.titleLarge)
        },
        text = {
            SettingsContent(
                onClearDBs = {
                    viewModel.clearAll()
                    onDismissRequest()
                },
                onClearUsers = {
                    viewModel.clearUserDB()
                    onDismissRequest()
                },
                onClearRepos = {
                    viewModel.clearRepoDB()
                    onDismissRequest()
                },
                onClearPageCache = {
                    viewModel.clearMediatorPageCaches()
                    onDismissRequest()
                }
            )
        }
    )
}

@Composable
fun SettingsContent(
    onClearDBs: () -> Unit,
    onClearUsers: () -> Unit,
    onClearRepos: () -> Unit,
    onClearPageCache: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Button(modifier = Modifier.width(200.dp), onClick = onClearDBs) {
            Icon(
                imageVector = Icons.Rounded.Delete,
                contentDescription = "clear all db"
            )
            Spacer(modifier = Modifier.width(30.dp))
            Text("Clear DB")
        }
        Button(modifier = Modifier.width(200.dp), onClick = onClearUsers) {
            Icon(
                imageVector = Icons.Rounded.Face,
                contentDescription = "clear user db"
            )
            Spacer(modifier = Modifier.width(30.dp))
            Text("Clear Users")
        }
        Button(modifier = Modifier.width(200.dp), onClick = onClearRepos) {
            Icon(
                imageVector = Icons.Rounded.ShoppingCart,
                contentDescription = "clear repo db"
            )
            Spacer(modifier = Modifier.width(30.dp))
            Text("Clear Repos")
        }
        Button(modifier = Modifier.width(200.dp), onClick = onClearPageCache) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = "clear user repos page db"
            )
            Spacer(modifier = Modifier.width(30.dp))
            Text("Clear page cache")
        }
    }
}

@Preview
@Composable
fun PreviewSettingsContent() {
    SettingsContent(
        onClearDBs = {},
        onClearUsers = {},
        onClearRepos = {},
        onClearPageCache = {})
}