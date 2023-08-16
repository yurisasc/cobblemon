/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.mixin;

import com.cobblemon.mod.common.brewing.BrewingRecipes;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.BrewingStandScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BrewingStandScreenHandler.PotionSlot.class)
public class PotionSlotMixin {
    @Inject(
        method = "matches",
        at = @At(value = "HEAD"),
        cancellable = true
    )
    private static void matchesBrewable(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (BrewingRecipes.brewableItems.contains(stack.getItem())) {
            cir.setReturnValue(true);
        }
    }
}
