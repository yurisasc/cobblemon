package com.cobblemon.mod.common.api.spawning.fishing

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.fishing.FishingBait
import com.cobblemon.mod.common.api.fishing.FishingBaits
import com.cobblemon.mod.common.api.spawning.context.FishingSpawningContext
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnAction
import com.cobblemon.mod.common.api.spawning.detail.SpawnAction
import com.cobblemon.mod.common.api.spawning.influence.SpawningInfluence

class FishingBaitInfluence : SpawningInfluence {
    override fun affectAction(action: SpawnAction<*>) {
        if (action !is PokemonSpawnAction || action.ctx !is FishingSpawningContext) return
        val bait = FishingBaits.getFromItemStack(action.ctx.baitStack) ?: return

        bait.effects.forEach { baitEffect ->
            if (Math.random() <= baitEffect.chance) return
            when (baitEffect.type) {
                FishingBait.Effects.SHINY_REROLL -> shinyReroll(action, baitEffect)
            }
        }
    }

    private fun shinyReroll(action: PokemonSpawnAction, effect: FishingBait.Effect) {
        val shinyOdds = Cobblemon.config.shinyRate.toInt()
        val randomNumber = kotlin.random.Random.nextInt(0, shinyOdds + 1)

        if (randomNumber <= (effect.value).toInt()) {
            action.props.shiny = true
        }
    }
}