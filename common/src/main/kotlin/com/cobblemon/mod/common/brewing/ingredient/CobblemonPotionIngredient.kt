/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.brewing.ingredient

import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.PotionContentsComponent
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.potion.Potion
import net.minecraft.core.Holder
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Internal
class CobblemonPotionIngredient(val potion: Holder<Potion>) : CobblemonIngredient {

    override fun matches(stack: ItemStack): Boolean {
        return stack.get(DataComponentTypes.POTION_CONTENTS)?.matches(potion) ?: false
    }

    override fun matchingStacks(): List<ItemStack> {
        val list = arrayListOf<ItemStack>()
        list += PotionContentsComponent.createStack(Items.POTION, potion)
        list += PotionContentsComponent.createStack(Items.SPLASH_POTION, potion)
        list += PotionContentsComponent.createStack(Items.LINGERING_POTION, potion)
        return list
    }

}