package com.cablemc.pokemoncobbled.common.api.blocks

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.valueproviders.UniformInt
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Material

open class EvolutionStoneOre(properties: Properties, var xpRange: UniformInt = UniformInt.of(1, 2)) : Block(properties) {
    companion object {
        const val NORMAL_DESTROY_TIME = 3.0F
        const val DEEPSLATE_DESTROY_TIME = 4.5F
        const val EXPLOSION_RESISTANCE = 3.0F
        val NORMAL_PROPERTIES: Properties = Properties.of(Material.STONE)
            .requiresCorrectToolForDrops()
            .strength(NORMAL_DESTROY_TIME, EXPLOSION_RESISTANCE)
        val DEEPSLATE_PROPERTIES: Properties = Properties.of(Material.STONE)
            .requiresCorrectToolForDrops()
            .strength(DEEPSLATE_DESTROY_TIME, EXPLOSION_RESISTANCE)
            .sound(SoundType.DEEPSLATE)
    }

    override fun spawnAfterBreak(
        blockState: BlockState,
        serverLevel: ServerLevel,
        blockPos: BlockPos,
        itemStack: ItemStack
    ) {
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, itemStack) == 0) {
            val xp = xpRange.sample(serverLevel.random)
            if (xp > 0)
                popExperience(serverLevel, blockPos, xp)
        }
    }
}
