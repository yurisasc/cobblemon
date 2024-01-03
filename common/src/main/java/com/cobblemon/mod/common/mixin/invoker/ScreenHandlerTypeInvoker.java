package com.cobblemon.mod.common.mixin.invoker;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.ScreenHandlerType.Factory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ScreenHandlerType.class)
public interface ScreenHandlerTypeInvoker {
    @Invoker("<init>")
    static<T extends ScreenHandler> ScreenHandlerType<T> cobblemon$create(Factory<T> factory, FeatureSet requiredFeatures) {
        throw new UnsupportedOperationException();
    }
}
