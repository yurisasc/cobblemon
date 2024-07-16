package com.cobblemon.mod.common.client.tooltips

import com.cobblemon.mod.common.api.fishing.FishingBaits
import com.cobblemon.mod.common.api.fishing.PokeRods
import com.cobblemon.mod.common.api.pokeball.PokeBalls
import com.cobblemon.mod.common.api.text.gray
import com.cobblemon.mod.common.item.interactive.PokerodItem
import net.minecraft.client.Minecraft
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack

object FishingRodTooltipGenerator : TooltipGenerator() {
    override fun generateTooltip(stack: ItemStack, lines: MutableList<Component>): MutableList<Component>? {
        val resultLines = mutableListOf<Component>()

        val rod = (stack.item as? PokerodItem)?.pokeRodId?.let { PokeRods.getPokeRod(it) } ?: return null
        val ball = PokeBalls.getPokeBall(rod.pokeBallId) ?: return null

        // Add the description of the Poke Ball used in the rod
        ball.item.description.let {
            val bobberDescription = Component.literal("Bobber: ").append(it.copy().gray())
            resultLines.add(bobberDescription)
        }

        val client = Minecraft.getInstance()
        val itemRegistry = client.level?.registryAccess()?.registryOrThrow(Registries.ITEM)
        itemRegistry?.let { registry ->
            FishingBaits.getFromRodItemStack(stack)?.toItemStack(registry)?.item?.description?.copy()?.gray()?.let {
                resultLines.add(Component.literal("Bait: ").append(it))
            }
        }

        return resultLines
    }
}