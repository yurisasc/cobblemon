package com.cobblemon.mod.common.client.tooltips

import com.cobblemon.mod.common.api.fishing.FishingBaits
import com.cobblemon.mod.common.api.text.blue
import com.cobblemon.mod.common.api.types.ElementalTypes
import com.cobblemon.mod.common.item.interactive.PokerodItem
import com.cobblemon.mod.common.pokemon.Gender
import com.cobblemon.mod.common.util.lang
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import java.math.BigDecimal
import java.text.DecimalFormat

object FishingBaitTooltipGenerator : TooltipGenerator() {
    private val fishingBaitHeader by lazy { lang("fishing_bait_effect_header").blue() }

    override fun generateAdditionalTooltip(stack: ItemStack, lines: MutableList<Component>): MutableList<Component>? {
        val resultLines = mutableListOf<Component>()
        val bait =
            (if (stack.item is PokerodItem) PokerodItem.getBaitOnRod(stack) else FishingBaits.getFromBaitItemStack(stack))
                ?: return null
        // copied from berryitem
        resultLines.addLast(Component.empty()) // blank line
        resultLines.addLast(this.fishingBaitHeader)

        val formatter = DecimalFormat("0.##")

        bait.effects.forEach { effect ->
            // TODO("Parse lang from effect, remove hardcoded references through codebase")
            val effectType = effect.type.path.toString()
            val effectSubcategory = effect.subcategory?.path.toString()
            var effectChance = effect.chance * 100
            var effectValue = when (effectType) {
                "bite_time" -> (effect.value * 100).toInt()
                else -> effect.value.toInt()
            }
            val subcategoryString = when (effectType) {
                "nature", "ev", "iv" -> com.cobblemon.mod.common.api.pokemon.stats.Stats.getStat(
                    effectSubcategory
                ).name
                    .split('_')
                    .joinToString(" ") { it.lowercase().replaceFirstChar { char -> char.uppercase() } }

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
                effectValue++
            }

            resultLines.addLast(
                lang(
                    "fishing_bait_effects.$effectType.tooltip",
                    formatter.format(effectChance),
                    subcategoryString,
                    formatter.format(effectValue)
                )
            )
        }

        return resultLines
    }
}