package com.cobblemon.mod.common.gui

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.mixin.invoker.ScreenHandlerTypeInvoker
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.resource.featuretoggle.FeatureFlags
import net.minecraft.screen.ScreenHandlerType.Factory
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.text.Text
import net.minecraft.util.Identifier

object CobblemonScreenHandlers {
    //val TMM_SCREEN = register(cobblemonResource("tmm_screen"), ::TMMScreenHandler)
    val COOKING_POT_SCREEN = register(cobblemonResource("cooking_pot_screen")) { syncId, playerInventory ->
        CookingPotScreenHandler(syncId, playerInventory, ScreenHandlerContext.EMPTY)
    }

    fun <T : ScreenHandler> register(identifier: Identifier, factory: Factory<T>): ScreenHandlerType<T> {

        val result = ScreenHandlerTypeInvoker.`cobblemon$create`(factory, FeatureFlags.VANILLA_FEATURES)
        Cobblemon.implementation.registerScreenHandlerType(identifier, result)
        return result
    }

}