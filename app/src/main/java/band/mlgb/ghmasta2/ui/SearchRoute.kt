package band.mlgb.ghmasta2.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import band.mlgb.ghmasta2.model.RepoEntity
import band.mlgb.ghmasta2.model.UserEntity
import band.mlgb.ghmasta2.navigation.navigateToUserRepos
import band.mlgb.ghmasta2.network.Resource
import band.mlgb.ghmasta2.ui.theme.GHMasta2Theme
import band.mlgb.ghmasta2.viewmodels.SearchViewModel


sealed interface SearchState {
    data object Searching : SearchState
    class Result(
        val lazyPagingItems: LazyPagingItems<RepoEntity>,
        val userResult: Resource<UserEntity?>
    ) : SearchState
}

sealed interface RecentSearchState {
    data object Loading : RecentSearchState
    class Result(
        val entries: List<String> = emptyList()
    ) : RecentSearchState

    fun hasResult() =
        this is Result && this.entries.isNotEmpty()
}

@Composable
fun SearchRoute(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.currentQuery.collectAsStateWithLifecycle()

    val searchReposLazyPagingItems =
        viewModel.searchKeywordInMemoryPagingFlow.collectAsLazyPagingItems()

    val userSearchResult by viewModel.userSearchResultFlow.collectAsStateWithLifecycle()

    val recentSearchesResult by viewModel.recentSearchQueryFlow.collectAsStateWithLifecycle()

    // This is awkward, ideally this should be done by combining two flows in viewmodel, but
    //  viewModel.searchUserRepoMediatorPagingFlow cannot be combined as regular flow.
    val searchState by remember(searchReposLazyPagingItems) {
        derivedStateOf {
            if (userSearchResult is Resource.Loading && searchReposLazyPagingItems.isSearching()) {
                SearchState.Searching
            } else {
                SearchState.Result(
                    searchReposLazyPagingItems,
                    userSearchResult
                )
            }
        }
    }


    SearchScreen(
        searchQuery = searchQuery,
        searchState = searchState,
        recentSearchState = recentSearchesResult,
        onSearchQueryChanged = viewModel::onQueryChanged,
        onBackClicked = { navController.navigate(STARRED_REPO_ROUTE) },
        onRepoStarClicked = { repoEntity ->
            viewModel.upsertRepo(repoEntity)
        },
        onUserClicked = navController::navigateToUserRepos,
        onUserLikeClicked = { userId, liked ->
            if (liked) {
                viewModel.likeUser(userId)
            } else {
                viewModel.unlikeUser(userId)
            }
        },
        onClearRecentEntries = {
            viewModel.clearRecentQueries()
        },
        onAddRecentEntry = { entryToAdd ->
            viewModel.addNewRecentEntry(entryToAdd)
        }

    )

}

@Composable
fun SearchScreen(
    searchQuery: String,
    searchState: SearchState,
    recentSearchState: RecentSearchState,
    onSearchQueryChanged: (String) -> Unit = {},
    onBackClicked: () -> Unit = {},
    onRepoStarClicked: (RepoEntity) -> Unit = { _ -> },
    onUserClicked: (String) -> Unit = { _ -> },
    onUserLikeClicked: (Long, Boolean) -> Unit = { _, _ -> },
    onClearRecentEntries: () -> Unit = {},
    onAddRecentEntry: (String) -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxSize()) {
        SearchTopBar(
            searchQuery,
            onSearchQueryChanged,
            onBackClicked,
            onAddRecentEntry
        )
        RecentSearch(recentSearchState, onClearRecentEntries, onSearchQueryChanged)
        when (searchState) {
            is SearchState.Result -> {
                ResultView(
                    userResult = searchState.userResult,
                    onUserLikeClicked = onUserLikeClicked,
                    repos = searchState.lazyPagingItems,
                    onRepoStarClicked = onRepoStarClicked,
                    onUserClicked = onUserClicked
                )
            }

            SearchState.Searching -> Loading()
        }
    }

}

@Composable
fun RecentSearch(
    recentSearchState: RecentSearchState,
    onClearAll: () -> Unit,
    onSelectRecentEntry: (String) -> Unit
) {

    SectionLabel(
        title = "Recent Searches",
        onClickClear = if (recentSearchState.hasResult()) onClearAll else null
    )
    when (recentSearchState) {
        RecentSearchState.Loading -> Loading()
        is RecentSearchState.Result -> {
            if (recentSearchState.entries.isEmpty()) {
                Text(
                    modifier = Modifier
                        .padding(top = 5.dp, bottom = 5.dp)
                        .fillMaxWidth(),
                    text = "no recent entries",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            } else {
                LazyColumn {
                    items(recentSearchState.entries) {
                        Text(
                            modifier = Modifier
                                .padding(start = 20.dp, top = 5.dp, bottom = 5.dp)
                                .clickable {
                                    onSelectRecentEntry(it)
                                },
                            text = it,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Start,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchTopBar(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onBackClicked: () -> Unit,
    onAddRecentEntry: (String) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClicked) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack, ""
            )
        }
        TextField(
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp, vertical = 5.dp)
                .focusRequester(focusRequester),
            leadingIcon = {
                Icon(imageVector = Icons.Rounded.Search, contentDescription = "Search icon")
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    keyboardController?.hide()
                    onAddRecentEntry(searchQuery)
                },
            ),
            shape = RoundedCornerShape(30.dp),
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            maxLines = 1,
            singleLine = true
        )
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
private fun ResultView(
    userResult: Resource<UserEntity?>,
    onUserLikeClicked: (Long, Boolean) -> Unit = { _, _ -> },
    repos: LazyPagingItems<RepoEntity>,
    onRepoStarClicked: (RepoEntity) -> Unit,
    onUserClicked: (String) -> Unit,
) {
    // we only know userResult and repos are not searching,
    // need to handle the non empty case and empty case

    if (userResult is Resource.Result && userResult.data == null && repos.noResults()) {
        Text(
            text = "No results found",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
    } else {
        userResult.data?.let { user ->
            SectionLabel(title = "Found User")
            UserView(
                user = user,
                onUserClicked = onUserClicked,
            ) { liked ->
                onUserLikeClicked(user.id, liked)
            }
        }

        if (repos.noResults().not()) {
            SectionLabel(title = "Found Repos")
            LazyPagingItemsView(
                lazyPagingItems = repos,
                onRepoStarClicked = { repoEntity, starred ->
                    onRepoStarClicked(repoEntity.copy(starred = starred))
                }
            ) { userClicked ->
                onUserClicked(userClicked.login)
            }
        }
    }
}

@Composable
private fun SectionLabel(title: String, onClickClear: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )

        onClickClear?.let {
            IconButton(
                onClick = onClickClear
            ) {
                Icon(
                    imageVector = Icons.Rounded.Clear,
                    contentDescription = "Clear button on section label"
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewSectionLabel() {
    GHMasta2Theme {
        Column {
            SectionLabel(title = "MLGB with button", {})

            SectionLabel(title = "MLGB without button")
        }
    }
}

@Preview
@Composable
fun PreviewRecentSearch() {
    GHMasta2Theme {
        Column {
            RecentSearch(
                recentSearchState = RecentSearchState.Result(entries = listOf("mlgb", "bglm")),
                onClearAll = { },
                onSelectRecentEntry = {}
            )
            RecentSearch(
                recentSearchState = RecentSearchState.Result(entries = listOf()),
                onClearAll = { },
                onSelectRecentEntry = {}
            )
            RecentSearch(
                recentSearchState = RecentSearchState.Loading,
                onClearAll = { },
                onSelectRecentEntry = {}
            )
        }
    }
}