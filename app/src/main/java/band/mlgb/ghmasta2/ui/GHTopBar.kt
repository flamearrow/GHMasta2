package band.mlgb.ghmasta2.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GHTopBar(
    title: String,
    onNavigationClick: () -> Unit,
    onSettingsClick: () -> Unit // change themes etc.
) {
    CenterAlignedTopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = onNavigationClick) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "Search icon",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(imageVector = Icons.Rounded.Settings, contentDescription = "Setting Icon")
            }
        }
    )
}

@Preview("Top App Bar")
@Composable
private fun GHTopbarPreview() {

    GHTopBar("title", {}, {})
}
