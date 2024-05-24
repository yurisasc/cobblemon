package com.cobblemon.mod.common.api.spawning.fishing

import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnAction
import net.minecraft.item.ItemStack

class ShinyBaitInfluence: FishingBaitInfluence() {
    override fun affectFishingAction(action: PokemonSpawnAction, baitStack: ItemStack, rodStack: ItemStack) {
        println("Yaaaay we're here")
    }
}