package com.cobblemon.mod.common.mixin.accessor;

import net.minecraft.client.gui.widget.EntryListWidget;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntryListWidget.class)
public interface EntryListWidgetAccessor {
    @Accessor("renderBackground")
    boolean getRenderBackground();

    @Accessor("renderHeader")
    boolean getRenderHeader();

    @Accessor("renderHorizontalShadows")
    boolean getRenderHorizontalShadows();
}
