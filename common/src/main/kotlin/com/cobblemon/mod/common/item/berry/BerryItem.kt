/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.item.berry

import com.cobblemon.mod.common.api.fishing.FishingBaits
import com.cobblemon.mod.common.api.fishing.PokeRods
import com.cobblemon.mod.common.api.pokeball.PokeBalls
import com.cobblemon.mod.common.api.text.blue
import com.cobblemon.mod.common.api.text.gray
import com.cobblemon.mod.common.api.types.ElementalTypes
import com.cobblemon.mod.common.block.BerryBlock
import com.cobblemon.mod.common.item.interactive.PokerodItem
import com.cobblemon.mod.common.item.interactive.PokerodItem.Companion.getBaitEffects
import com.cobblemon.mod.common.pokemon.Gender
import com.cobblemon.mod.common.util.lang
import net.minecraft.client.Minecraft
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemNameBlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import java.math.BigDecimal
import java.text.DecimalFormat

open class BerryItem(private val berryBlock: BerryBlock) : ItemNameBlockItem(berryBlock, Properties()) {

    override fun appendHoverText(
        stack: ItemStack,
        tooltipContext: TooltipContext,
        tooltip: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {

        if (FishingBaits.isFishingBait(stack)) {
            // add tooltip for header
            tooltip.addLast(Component.literal("")) // blank line
            tooltip.addLast(lang("fishing_bait_effect_header").blue())

            val formatter = DecimalFormat("0.##")

            // Retrieve and add bait effect tooltips
            FishingBaits.getFromBaitItemStack(stack)?.effects?.forEach { effect ->
                val effectType = effect.type.path.toString()
                val effectSubcategory = effect.subcategory?.path.toString()
                var effectChance = effect.chance * 100
                val effectValue = when (effectType) {
                    "bite_time" -> (effect.value * 100).toInt()
                    else -> effect.value.toInt()
                }
                val subcategoryString = when (effectType) {
                    "nature", "ev", "iv" -> com.cobblemon.mod.common.api.pokemon.stats.Stats.getStat(effectSubcategory)?.name
                        ?.split('_')
                        ?.joinToString(" ") { it.lowercase().replaceFirstChar { char -> char.uppercase() } }
                        ?: ""
                    "gender" -> Gender.valueOf(effectSubcategory).name
                        .split('_')
                        .joinToString(" ") { it.lowercase().replaceFirstChar { char -> char.uppercase() } }
                    "tera" -> ElementalTypes.get(effectSubcategory)?.name
                        ?.split('_')
                        ?.joinToString(" ") { it.lowercase().replaceFirstChar { char -> char.uppercase() } }
                        ?: ""
                    else -> ""
                }

                // handle reformatting of shiny chance effectChance
                if (effectType == "shiny_reroll") {
                    effectChance = BigDecimal((effectChance / 100.0) + 1).setScale(2, BigDecimal.ROUND_HALF_EVEN).toDouble()
                }

                tooltip.addLast(
                    lang(
                        "fishing_bait_effects.$effectType.tooltip",
                        formatter.format(effectChance),
                        subcategoryString,
                        formatter.format(effectValue)
                    )
                )
            }
        }

        super.appendHoverText(stack, tooltipContext, tooltip, tooltipFlag)
    }

    fun berry() = this.berryBlock.berry()
}