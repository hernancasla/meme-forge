package com.memeforge.ui.editor;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import androidx.lifecycle.ViewModel;
import com.memeforge.data.model.MemeTemplate;
import com.memeforge.data.model.MemeText;
import com.memeforge.data.repository.TemplateRepository;
import com.memeforge.util.MemeRenderer;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;
import kotlinx.coroutines.flow.StateFlow;
import javax.inject.Inject;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0007\u0018\u00002\u00020\u0001B\u0019\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0006\u0010\u000e\u001a\u00020\u000fJ\u000e\u0010\u0010\u001a\u00020\u000f2\u0006\u0010\u0011\u001a\u00020\u0012J\u000e\u0010\u0013\u001a\u00020\u000f2\u0006\u0010\u0014\u001a\u00020\u0015J\u000e\u0010\u0016\u001a\u00020\u000f2\u0006\u0010\u0011\u001a\u00020\u0012J\u0012\u0010\u0017\u001a\u0004\u0018\u00010\u00182\u0006\u0010\u0019\u001a\u00020\u0012H\u0002J\u0016\u0010\u001a\u001a\u00020\u000f2\u0006\u0010\u001b\u001a\u00020\u00152\u0006\u0010\u001c\u001a\u00020\u0015R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r\u00a8\u0006\u001d"}, d2 = {"Lcom/memeforge/ui/editor/EditorViewModel;", "Landroidx/lifecycle/ViewModel;", "repository", "Lcom/memeforge/data/repository/TemplateRepository;", "context", "Landroid/content/Context;", "(Lcom/memeforge/data/repository/TemplateRepository;Landroid/content/Context;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/memeforge/ui/editor/EditorUiState;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "clearError", "", "generatePreview", "sourceBitmap", "Landroid/graphics/Bitmap;", "loadTemplate", "templateId", "", "save", "saveBitmapToGallery", "Landroid/net/Uri;", "bitmap", "updateText", "zoneId", "content", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class EditorViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.memeforge.data.repository.TemplateRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.memeforge.ui.editor.EditorUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.memeforge.ui.editor.EditorUiState> uiState = null;
    
    @javax.inject.Inject()
    public EditorViewModel(@org.jetbrains.annotations.NotNull()
    com.memeforge.data.repository.TemplateRepository repository, @dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.memeforge.ui.editor.EditorUiState> getUiState() {
        return null;
    }
    
    public final void loadTemplate(@org.jetbrains.annotations.NotNull()
    java.lang.String templateId) {
    }
    
    public final void updateText(@org.jetbrains.annotations.NotNull()
    java.lang.String zoneId, @org.jetbrains.annotations.NotNull()
    java.lang.String content) {
    }
    
    public final void generatePreview(@org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap sourceBitmap) {
    }
    
    public final void save(@org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap sourceBitmap) {
    }
    
    private final android.net.Uri saveBitmapToGallery(android.graphics.Bitmap bitmap) {
        return null;
    }
    
    public final void clearError() {
    }
}