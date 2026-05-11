package com.memeforge.ui.home

import androidx.lifecycle.ViewModel
import com.memeforge.data.model.MemeTemplate
import com.memeforge.data.repository.TemplateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class HomeUiState(
    val templates: List<MemeTemplate> = emptyList(),
    val filteredTemplates: List<MemeTemplate> = emptyList(),
    val selectedCategory: String = "all",
    val searchQuery: String = ""
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TemplateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        val templates = repository.getTemplates()
        _uiState.value = HomeUiState(templates = templates, filteredTemplates = templates)
    }

    fun onCategorySelected(category: String) {
        val current = _uiState.value
        _uiState.value = current.copy(
            selectedCategory = category,
            filteredTemplates = filter(current.templates, category, current.searchQuery)
        )
    }

    fun onSearchQueryChanged(query: String) {
        val current = _uiState.value
        _uiState.value = current.copy(
            searchQuery = query,
            filteredTemplates = filter(current.templates, current.selectedCategory, query)
        )
    }

    private fun filter(templates: List<MemeTemplate>, category: String, query: String) =
        templates.filter { t ->
            (category == "all" || t.category == category) &&
                (query.isEmpty() || t.name.contains(query, ignoreCase = true))
        }
}
