package com.cobblemon.mod.common.gui

import com.cobblemon.mod.common.CobblemonBlocks
import com.cobblemon.mod.common.block.entity.CookingPotBlockEntity
import net.minecraft.block.Blocks
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.CraftingInventory
import net.minecraft.inventory.CraftingResultInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.RecipeInputInventory
import net.minecraft.item.ItemStack
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket
import net.minecraft.recipe.CraftingRecipe
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeMatcher
import net.minecraft.recipe.RecipeType
import net.minecraft.recipe.book.RecipeBookCategory
import net.minecraft.screen.AbstractRecipeScreenHandler
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.CraftingResultSlot
import net.minecraft.screen.slot.Slot
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class CookingPotScreenHandler : AbstractRecipeScreenHandler<RecipeInputInventory> {
    val input: CraftingInventory
    val result: CraftingResultInventory
    val context: ScreenHandlerContext
    val player: PlayerEntity
    var inventory: Inventory? = null

    companion object {
        const val RESULT_ID = 0
        private const val INPUT_START = 1
        private const val INPUT_END = 10
        private const val INVENTORY_START = 10
        private const val INVENTORY_END = 37
        private const val HOTBAR_START = 37
        private const val HOTBAR_END = 46

        fun updateResult(
            handler: ScreenHandler,
            world: World,
            player: PlayerEntity,
            craftingInventory: RecipeInputInventory,
            resultInventory: CraftingResultInventory
        ) {
            if (world.isClient) return
            val serverPlayerEntity = player as ServerPlayerEntity
            var itemStack = ItemStack.EMPTY
            val optional = world.server?.recipeManager?.getFirstMatch(
                RecipeType.CRAFTING, craftingInventory, world
            )
            if (optional != null) {
                if (optional.isPresent) {
                    val craftingRecipe = optional.get()
                    val itemStack2 = craftingRecipe.craft(craftingInventory, world.registryManager)
                    if (itemStack2.isItemEnabled(world.enabledFeatures)) {
                        itemStack = itemStack2
                    }
                }
            }
            resultInventory.setStack(0, itemStack)
            handler.setPreviousTrackedSlot(0, itemStack)
            serverPlayerEntity.networkHandler.sendPacket(
                ScreenHandlerSlotUpdateS2CPacket(handler.syncId, handler.nextRevision(), 0, itemStack)
            )
        }
    }

    constructor(syncId: Int, playerInventory: PlayerInventory, context: ScreenHandlerContext) :
            super(ScreenHandlerType.CRAFTING, syncId) {
        this.context = context
        this.player = playerInventory.player
        this.input = CraftingInventory(this, 3, 3)
        this.result = CraftingResultInventory()
        initializeSlots(playerInventory)
    }

    constructor(syncId: Int, playerInventory: PlayerInventory, cookingPotInventory: CookingPotBlockEntity.CookingPotBlockInventory, cookingPotBlockEntity: CookingPotBlockEntity) :
            super(ScreenHandlerType.CRAFTING, syncId) {
        this.context = ScreenHandlerContext.EMPTY
        this.player = playerInventory.player
        this.input = CraftingInventory(this, 3, 3)
        this.result = CraftingResultInventory()
        this.inventory = cookingPotInventory
        initializeSlots(playerInventory)
    }

    private fun initializeSlots(playerInventory: PlayerInventory) {
        addSlot(CraftingResultSlot(playerInventory.player, input, result, 0, 124, 35))
        for (i in 0..2) {
            for (j in 0..2) {
                addSlot(Slot(input, j + i * 3, 30 + j * 18, 17 + i * 18))
            }
        }
        for (i in 0..2) {
            for (j in 0..8) {
                addSlot(Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18))
            }
        }
        for (i in 0..8) {
            addSlot(Slot(playerInventory, i, 8 + i * 18, 142))
        }
    }

    override fun onContentChanged(inventory: Inventory) {
        context.run { world, pos -> updateResult(this@CookingPotScreenHandler, world, player, input, result) }
    }

    override fun populateRecipeFinder(finder: RecipeMatcher) {
        input.provideRecipeInputs(finder)
    }

    override fun clearCraftingSlots() {
        input.clear()
        result.clear()
    }

    override fun matches(recipe: Recipe<in RecipeInputInventory>): Boolean {
        return recipe.matches(input, player.world)
    }

    override fun onClosed(player: PlayerEntity) {
        super.onClosed(player)
        context.run { world, pos -> dropInventory(player, input) }
    }

    override fun canUse(player: PlayerEntity): Boolean {
        return canUse(context, player, CobblemonBlocks.COOKING_POT)
    }

    override fun quickMove(player: PlayerEntity, slot: Int): ItemStack {
        var itemStack = ItemStack.EMPTY
        val slot2 = slots[slot] as Slot
        if (slot2.hasStack()) {
            val itemStack2 = slot2.stack
            itemStack = itemStack2.copy()
            if (slot == 0) {
                context.run { world, pos -> itemStack2.item.onCraft(itemStack2, world, player) }
                if (!insertItem(itemStack2, 10, 46, true)) {
                    return ItemStack.EMPTY
                }
                slot2.onQuickTransfer(itemStack2, itemStack)
            } else if (slot in 10 until 46) {
                if (!insertItem(itemStack2, 1, 10, false) && (slot < 37 && !insertItem(itemStack2, 37, 46, false) || !insertItem(itemStack2, 10, 37, false))) {
                    return ItemStack.EMPTY
                }
            } else if (!insertItem(itemStack2, 10, 46, false)) {
                return ItemStack.EMPTY
            }
            if (itemStack2.isEmpty) {
                slot2.stack = ItemStack.EMPTY
            } else {
                slot2.markDirty()
            }
            if (itemStack2.count == itemStack.count) {
                return ItemStack.EMPTY
            }
            slot2.onTakeItem(player, itemStack2)
            if (slot == 0) {
                player.dropItem(itemStack2, false)
            }
        }
        return itemStack
    }

    override fun canInsertIntoSlot(stack: ItemStack, slot: Slot): Boolean {
        return slot.inventory !== result && super.canInsertIntoSlot(stack, slot)
    }

    override fun getCraftingResultSlotIndex(): Int {
        return RESULT_ID
    }

    override fun getCraftingWidth(): Int {
        return input.width
    }

    override fun getCraftingHeight(): Int {
        return input.height
    }

    override fun getCraftingSlotCount(): Int {
        return 10
    }

    override fun getCategory(): RecipeBookCategory {
        return RecipeBookCategory.CRAFTING
    }

    override fun canInsertIntoSlot(index: Int): Boolean {
        return index != craftingResultSlotIndex
    }
}
