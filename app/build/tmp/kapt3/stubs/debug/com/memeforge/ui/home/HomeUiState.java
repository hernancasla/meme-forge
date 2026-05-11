package com.memeforge.ui.home;

import androidx.lifecycle.ViewModel;
import com.memeforge.data.model.MemeTemplate;
import com.memeforge.data.repository.TemplateRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.StateFlow;
import javax.inject.Inject;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u000e\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B9\u0012\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\tJ\u000f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\u000f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u0013\u001a\u00020\u0007H\u00c6\u0003J=\u0010\u0014\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u0007H\u00c6\u0001J\u0013\u0010\u0015\u001a\u00020\u00162\b\u0010\u0017\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0018\u001a\u00020\u0019H\u00d6\u0001J\t\u0010\u001a\u001a\u00020\u0007H\u00d6\u0001R\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\b\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\rR\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u000b\u00a8\u0006\u001b"}, d2 = {"Lcom/memeforge/ui/home/HomeUiState;", "", "templates", "", "Lcom/memeforge/data/model/MemeTemplate;", "filteredTemplates", "selectedCategory", "", "searchQuery", "(Ljava/util/List;Ljava/util/List;Ljava/lang/String;Ljava/lang/String;)V", "getFilteredTemplates", "()Ljava/util/List;", "getSearchQuery", "()Ljava/lang/String;", "getSelectedCategory", "getTemplates", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "", "toString", "app_debug"})
public final class HomeUiState {
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.memeforge.data.model.MemeTemplate> templates = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.memeforge.data.model.MemeTemplate> filteredTemplates = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String selectedCategory = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String searchQuery = null;
    
    public HomeUiState(@org.jetbrains.annotations.NotNull()
    java.util.List<com.memeforge.data.model.MemeTemplate> templates, @org.jetbrains.annotations.NotNull()
    java.util.List<com.memeforge.data.model.MemeTemplate> filteredTemplates, @org.jetbrains.annotations.NotNull()
    java.lang.String selectedCategory, @org.jetbrains.annotations.NotNull()
    java.lang.String searchQuery) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.memeforge.data.model.MemeTemplate> getTemplates() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.memeforge.data.model.MemeTemplate> getFilteredTemplates() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getSelectedCategory() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getSearchQuery() {
        return null;
    }
    
    public HomeUiState() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.memeforge.data.model.MemeTemplate> component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.memeforge.data.model.MemeTemplate> component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.memeforge.ui.home.HomeUiState copy(@org.jetbrains.annotations.NotNull()
    java.util.List<com.memeforge.data.model.MemeTemplate> templates, @org.jetbrains.annotations.NotNull()
    java.util.List<com.memeforge.data.model.MemeTemplate> filteredTemplates, @org.jetbrains.annotations.NotNull()
    java.lang.String selectedCategory, @org.jetbrains.annotations.NotNull()
    java.lang.String searchQuery) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}