package com.memeforge.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.memeforge.R
import com.memeforge.ui.components.AdBanner
import com.memeforge.ui.components.TemplateCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onTemplateClick: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val categories = listOf("all", "classic", "trending", "reaction")

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.app_name)) })
        },
        bottomBar = { AdBanner() }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChanged,
                label = { Text(stringResource(R.string.search_hint)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true
            )

            ScrollableTabRow(
                selectedTabIndex = categories.indexOf(uiState.selectedCategory).coerceAtLeast(0)
            ) {
                categories.forEach { category ->
                    Tab(
                        selected = uiState.selectedCategory == category,
                        onClick = { viewModel.onCategorySelected(category) },
                        text = {
                            Text(
                                when (category) {
                                    "all" -> stringResource(R.string.category_all)
                                    "classic" -> stringResource(R.string.category_classic)
                                    "trending" -> stringResource(R.string.category_trending)
                                    "reaction" -> stringResource(R.string.category_reaction)
                                    else -> category
                                }
                            )
                        }
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.filteredTemplates, key = { it.id }) { template ->
                    TemplateCard(
                        template = template,
                        onClick = { onTemplateClick(template.id) }
                    )
                }
            }
        }
    }
}
