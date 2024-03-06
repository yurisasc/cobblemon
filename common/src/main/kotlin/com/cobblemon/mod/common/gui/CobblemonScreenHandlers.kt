package com.cobblemon.mod.common.gui

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.mixin.invoker.ScreenHandlerTypeInvoker
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.registry.Registries
import net.minecraft.resource.featuretoggle.FeatureFlags
import net.minecraft.screen.ScreenHandlerType.Factory
import net.minecraft.resource.featuretoggle.FeatureSet
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.Identifier

object CobblemonScreenHandlers {
    val TMM_SCREEN = register(cobblemonResource("tmm_screen"), ::TMMScreenHandler)

    fun <T : ScreenHandler> register(identifier: Identifier, factory: Factory<T>): ScreenHandlerType<T> {

        val result = ScreenHandlerTypeInvoker.`cobblemon$create`(factory, FeatureFlags.VANILLA_FEATURES)
        Cobblemon.implementation.registerScreenHandlerType(identifier, result)
        return result
    }

}