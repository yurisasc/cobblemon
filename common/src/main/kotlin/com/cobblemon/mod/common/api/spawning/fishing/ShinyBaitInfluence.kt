package com.cobblemon.mod.common.api.spawning.fishing

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.fishing.FishingBait
import com.cobblemon.mod.common.api.fishing.FishingBaits
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnAction
import net.minecraft.item.ItemStack

class ShinyBaitInfluence: FishingBaitInfluence() {
    override fun affectFishingAction(action: PokemonSpawnAction, baitStack: ItemStack, rodStack: ItemStack) {
        if (action.props.shiny != null) return

        val bait = FishingBaits.getFromItemStack(baitStack) ?: return
        if (!bait.effects.any { it.type == FishingBait.Effects.SHINY_REROLL }) return

        val effect = bait.effects.filter { it.type == FishingBait.Effects.SHINY_REROLL }.random()
        if (Math.random() <= effect.chance) return

        val shinyOdds = Cobblemon.config.shinyRate.toInt()
        val randomNumber = kotlin.random.Random.nextInt(0, shinyOdds + 1)

        if (randomNumber <= (effect.value).toInt()) {
            action.props.shiny = true
        }
    }
}