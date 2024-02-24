/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.fabric.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.BrewingStandScreenHandler;
import net.minecraft.screen.BrewingStandScreenHandler.PotionSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to allow the player to fix the stacking of potion items in the Brewing Stand
 * @author Aethen
 * @since 02/20/2024
 */
@Mixin(BrewingStandScreenHandler.class)
public abstract class BrewingStandScreenHandlerMixin {

    /**
     * Injects into 'quickMove' method of BrewingStandScreenHandler before it returns
     * @param player PlayerEntity that is moving the item
     * @param index Index of the slot that the item is being moved from
     * @param cir CallbackInfoReturnable that allows the return value to be modified
     */
    @Inject(method = "quickMove", at = @At(value = "RETURN"), cancellable = true)
    private void cobblemon$quickMove(PlayerEntity player, int index, CallbackInfoReturnable<ItemStack> cir) {
        // ItemStack that 'quickMove' method is about to return
        ItemStack returnStack = cir.getReturnValue();

        // Check if the item being moved is a single item, is not being moved from or to a potion slot,
        // and is a type that can be inserted into a potion slot
        if (returnStack.getCount() == 1 && (index < 0 || index > 2) && PotionSlot.matches(returnStack)) {
            BrewingStandScreenHandler handler = (BrewingStandScreenHandler) (Object) this;
            // Loop through the three potion slots
            for (int i = 0; i < 3; i++) {
                // ItemStack of the current potion slot
                ItemStack potionSlotStack = handler.getSlot(i).getStack();
                if (!potionSlotStack.isEmpty()) {
                    // Revert any stacking and return the item to the player inventory
                    // This is needed because the 'quickMove' method does not handle stacking properly
                    potionSlotStack.setCount(potionSlotStack.getCount() - returnStack.getCount());
                    player.getInventory().insertStack(returnStack);
                    break;
                }
            }
        }
    }
}
