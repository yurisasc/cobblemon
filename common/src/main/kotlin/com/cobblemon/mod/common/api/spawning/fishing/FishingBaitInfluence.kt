package com.cobblemon.mod.common.api.spawning.fishing

import com.cobblemon.mod.common.api.spawning.context.FishingSpawningContext
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnAction
import com.cobblemon.mod.common.api.spawning.detail.SpawnAction
import com.cobblemon.mod.common.api.spawning.influence.SpawningInfluence
import net.minecraft.item.ItemStack

abstract class FishingBaitInfluence : SpawningInfluence {
    override fun affectAction(action: SpawnAction<*>) {
        if (action !is PokemonSpawnAction || action.ctx !is FishingSpawningContext) return
        affectFishingAction(action, action.ctx.baitStack, action.ctx.rodStack)
    }

    abstract fun affectFishingAction(action: PokemonSpawnAction, baitStack: ItemStack, rodStack: ItemStack)
}