package com.cobblemon.mod.common.mixin.client;

import com.cobblemon.mod.common.client.gui.ScrollingWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EntryListWidget.class)
public abstract class EntryListWidgetMixin {

    @SuppressWarnings("ConstantValue")
    @ModifyConstant(method = "getEntryAtPosition", constant = @Constant(intValue = 4))
    public int cobblemon$adjustOnlyIfNecessary(int input) {
        if(ScrollingWidget.class.isInstance(this)) {
            return 0;
        }

        return input;
    }

}
