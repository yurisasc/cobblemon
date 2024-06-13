package com.cobblemon.mod.common.gui

import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.recipe.RecipeType
import net.minecraft.recipe.book.RecipeBookCategory
import net.minecraft.screen.PropertyDelegate
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.world.World


class CookingPotScreenHandler : AbstractCookingPotScreenHandler {
    constructor(syncId: Int, playerInventory: PlayerInventory?) : super(
        ScreenHandlerType.FURNACE,
        RecipeType.SMELTING,
        RecipeBookCategory.FURNACE,
        syncId,
        playerInventory
    )

    constructor(
        syncId: Int,
        playerInventory: PlayerInventory?,
        inventory: Inventory?,
        propertyDelegate: PropertyDelegate?
    ) : super(
        ScreenHandlerType.FURNACE,
        RecipeType.SMELTING,
        RecipeBookCategory.FURNACE,
        syncId,
        playerInventory,
        inventory,
        propertyDelegate
    )
}