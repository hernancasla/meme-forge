package com.memeforge.data.repository;

import android.content.Context;
import com.memeforge.R;
import com.memeforge.data.model.MemeTemplate;
import com.memeforge.data.model.TextZone;
import javax.inject.Inject;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\bJ\u0010\u0010\u000b\u001a\u0004\u0018\u00010\t2\u0006\u0010\f\u001a\u00020\rJ\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\t0\bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000f"}, d2 = {"Lcom/memeforge/data/repository/TemplateRepository;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "json", "Lkotlinx/serialization/json/Json;", "premiumTemplates", "", "Lcom/memeforge/data/model/MemeTemplate;", "getPremiumTemplates", "getTemplateById", "id", "", "getTemplates", "app_debug"})
public final class TemplateRepository {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.serialization.json.Json json = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.memeforge.data.model.MemeTemplate> premiumTemplates = null;
    
    @javax.inject.Inject()
    public TemplateRepository(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.memeforge.data.model.MemeTemplate> getTemplates() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.memeforge.data.model.MemeTemplate> getPremiumTemplates() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.memeforge.data.model.MemeTemplate getTemplateById(@org.jetbrains.annotations.NotNull()
    java.lang.String id) {
        return null;
    }
}