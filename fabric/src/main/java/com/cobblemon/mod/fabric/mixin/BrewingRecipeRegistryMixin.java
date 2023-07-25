/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.fabric.mixin;

import com.cobblemon.mod.common.CobblemonItems;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BrewingRecipeRegistry.class)
public class BrewingRecipeRegistryMixin {
    @Inject(
        method = "craft",
        at = @At(value = "HEAD"),
        cancellable = true
    )
    private static void beforeCraft(ItemStack ingredient, ItemStack input, CallbackInfoReturnable<ItemStack> cir) {
        // This exists because brewing is Item -> Item or Potion -> Potion - here we want to do Potion -> Item. It's annoying.
        if (ingredient.getItem() == CobblemonItems.MEDICINAL_LEEK && input.getItem() instanceof PotionItem && PotionUtil.getPotion(input) == Potions.WATER) {
            cir.setReturnValue(CobblemonItems.MEDICINAL_BREW.getDefaultStack());
        }
    }
}
