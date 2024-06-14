package com.cobblemon.mod.common.gui.cooking;

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget

@Environment(EnvType.CLIENT)
interface CookBookProvider {
    fun refreshCookBook()

    fun getCookBookWidget(): CookBookWidget
}
