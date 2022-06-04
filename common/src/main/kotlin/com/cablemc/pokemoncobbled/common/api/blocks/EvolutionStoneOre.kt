package com.cablemc.pokemoncobbled.common.api.blocks

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.intprovider.UniformIntProvider

open class EvolutionStoneOre(properties: Settings, var xpRange: UniformIntProvider = UniformIntProvider.create(1, 2)) : Block(properties) {
    companion object {
        const val NORMAL_DESTROY_TIME = 3.0F
        const val DEEPSLATE_DESTROY_TIME = 4.5F
        const val EXPLOSION_RESISTANCE = 3.0F
        val NORMAL_PROPERTIES: Settings = Settings.of(Material.STONE)
            .requiresTool()
            .strength(NORMAL_DESTROY_TIME, EXPLOSION_RESISTANCE)
        val DEEPSLATE_PROPERTIES: Settings = Settings.of(Material.STONE)
            .requiresTool()
            .strength(DEEPSLATE_DESTROY_TIME, EXPLOSION_RESISTANCE)
            .sounds(BlockSoundGroup.DEEPSLATE)
    }

    override fun onStacksDropped(
        blockState: BlockState,
        serverWorld: ServerWorld,
        blockPos: BlockPos,
        itemStack: ItemStack
    ) {
        if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, itemStack) == 0) {
            val xp = xpRange.get(serverWorld.random)
            if (xp > 0)
                dropExperience(serverWorld, blockPos, xp)
        }
    }
}
