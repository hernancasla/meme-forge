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

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0017\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001BU\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0014\b\u0002\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u0012\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\r\u0012\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\u0002\u0010\u000fJ\u000b\u0010\u001b\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u0015\u0010\u001c\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005H\u00c6\u0003J\u000b\u0010\u001d\u001a\u0004\u0018\u00010\tH\u00c6\u0003J\t\u0010\u001e\u001a\u00020\u000bH\u00c6\u0003J\u000b\u0010\u001f\u001a\u0004\u0018\u00010\rH\u00c6\u0003J\u000b\u0010 \u001a\u0004\u0018\u00010\u0006H\u00c6\u0003JY\u0010!\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\u0014\b\u0002\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u00052\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t2\b\b\u0002\u0010\n\u001a\u00020\u000b2\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\r2\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u0006H\u00c6\u0001J\u0013\u0010\"\u001a\u00020\u000b2\b\u0010#\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010$\u001a\u00020%H\u00d6\u0001J\t\u0010&\u001a\u00020\u0006H\u00d6\u0001R\u0013\u0010\u000e\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\n\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u0012R\u001d\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0013\u0010\b\u001a\u0004\u0018\u00010\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0013\u0010\f\u001a\u0004\u0018\u00010\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001a\u00a8\u0006\'"}, d2 = {"Lcom/memeforge/ui/editor/EditorUiState;", "", "template", "Lcom/memeforge/data/model/MemeTemplate;", "memeTexts", "", "", "Lcom/memeforge/data/model/MemeText;", "previewBitmap", "Landroid/graphics/Bitmap;", "isSaving", "", "savedUri", "Landroid/net/Uri;", "error", "(Lcom/memeforge/data/model/MemeTemplate;Ljava/util/Map;Landroid/graphics/Bitmap;ZLandroid/net/Uri;Ljava/lang/String;)V", "getError", "()Ljava/lang/String;", "()Z", "getMemeTexts", "()Ljava/util/Map;", "getPreviewBitmap", "()Landroid/graphics/Bitmap;", "getSavedUri", "()Landroid/net/Uri;", "getTemplate", "()Lcom/memeforge/data/model/MemeTemplate;", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "other", "hashCode", "", "toString", "app_debug"})
public final class EditorUiState {
    @org.jetbrains.annotations.Nullable()
    private final com.memeforge.data.model.MemeTemplate template = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Map<java.lang.String, com.memeforge.data.model.MemeText> memeTexts = null;
    @org.jetbrains.annotations.Nullable()
    private final android.graphics.Bitmap previewBitmap = null;
    private final boolean isSaving = false;
    @org.jetbrains.annotations.Nullable()
    private final android.net.Uri savedUri = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String error = null;
    
    public EditorUiState(@org.jetbrains.annotations.Nullable()
    com.memeforge.data.model.MemeTemplate template, @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, com.memeforge.data.model.MemeText> memeTexts, @org.jetbrains.annotations.Nullable()
    android.graphics.Bitmap previewBitmap, boolean isSaving, @org.jetbrains.annotations.Nullable()
    android.net.Uri savedUri, @org.jetbrains.annotations.Nullable()
    java.lang.String error) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.memeforge.data.model.MemeTemplate getTemplate() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Map<java.lang.String, com.memeforge.data.model.MemeText> getMemeTexts() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final android.graphics.Bitmap getPreviewBitmap() {
        return null;
    }
    
    public final boolean isSaving() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final android.net.Uri getSavedUri() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getError() {
        return null;
    }
    
    public EditorUiState() {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.memeforge.data.model.MemeTemplate component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Map<java.lang.String, com.memeforge.data.model.MemeText> component2() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final android.graphics.Bitmap component3() {
        return null;
    }
    
    public final boolean component4() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final android.net.Uri component5() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.memeforge.ui.editor.EditorUiState copy(@org.jetbrains.annotations.Nullable()
    com.memeforge.data.model.MemeTemplate template, @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, com.memeforge.data.model.MemeText> memeTexts, @org.jetbrains.annotations.Nullable()
    android.graphics.Bitmap previewBitmap, boolean isSaving, @org.jetbrains.annotations.Nullable()
    android.net.Uri savedUri, @org.jetbrains.annotations.Nullable()
    java.lang.String error) {
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