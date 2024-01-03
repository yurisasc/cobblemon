package com.cobblemon.mod.common.gui

import com.cobblemon.mod.common.mixin.invoker.ScreenHandlerTypeInvoker
import net.minecraft.resource.featuretoggle.FeatureFlags
import net.minecraft.resource.featuretoggle.FeatureSet
import net.minecraft.screen.ScreenHandlerType

object CobblemonScreenHandlers {
    val TMM_SCREEN = ScreenHandlerTypeInvoker.`cobblemon$create`(TMMScreenHandler.TMMScreenHandlerFactory, FeatureFlags.VANILLA_FEATURES)

}