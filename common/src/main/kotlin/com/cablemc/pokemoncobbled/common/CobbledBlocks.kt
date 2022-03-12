package com.cablemc.pokemoncobbled.common

import com.cablemc.pokemoncobbled.common.world.level.block.ApricornSaplingBlock
import dev.architectury.registry.registries.DeferredRegister
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Registry
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.LeavesBlock
import net.minecraft.world.level.block.RotatedPillarBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Material
import net.minecraft.world.level.material.MaterialColor

object CobbledBlocks {
    private val blockRegister = DeferredRegister.create(PokemonCobbled.MODID, Registry.BLOCK_REGISTRY)
    private fun <T : Block> queue(name: String, block: T) = blockRegister.register(name) { block }

    val APRICORN_LOG = queue("apricorn_log", log(MaterialColor.PODZOL, MaterialColor.COLOR_BROWN))
    val STRIPPED_APRICORN_LOG = queue("stripped_apricorn_log", log(MaterialColor.PODZOL, MaterialColor.PODZOL))
    val APRICORN_PLANKS = queue("apricorn_planks", Block(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.PODZOL)
        .strength(2.0f, 3.0f)
        .sound(SoundType.WOOD)))
    val APRICORN_LEAVES = queue("apricorn_leaves", leaves(SoundType.GRASS))

    val BLACK_APRICORN_SAPLING = queue("black_apricorn_sapling", ApricornSaplingBlock(BlockBehaviour.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS)))
    val BLUE_APRICORN_SAPLING = queue("blue_apricorn_sapling", ApricornSaplingBlock(BlockBehaviour.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS)))
    val GREEN_APRICORN_SAPLING = queue("green_apricorn_sapling", ApricornSaplingBlock(BlockBehaviour.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS)))
    val PINK_APRICORN_SAPLING = queue("pink_apricorn_sapling", ApricornSaplingBlock(BlockBehaviour.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS)))
    val RED_APRICORN_SAPLING = queue("red_apricorn_sapling", ApricornSaplingBlock(BlockBehaviour.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS)))
    val WHITE_APRICORN_SAPLING = queue("white_apricorn_sapling", ApricornSaplingBlock(BlockBehaviour.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS)))
    val YELLOW_APRICORN_SAPLING = queue("yellow_apricorn_sapling", ApricornSaplingBlock(BlockBehaviour.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS)))

    fun register() {
        blockRegister.register()
    }

    /**
     * Helper method for creating logs
     * copied over from Vanilla
     */
    private fun log(arg: MaterialColor, arg2: MaterialColor): RotatedPillarBlock {
        return RotatedPillarBlock(BlockBehaviour.Properties.of(Material.WOOD) { arg3: BlockState ->
            if (arg3.getValue(RotatedPillarBlock.AXIS) === Direction.Axis.Y) arg else arg2
        }.strength(2.0f).sound(SoundType.WOOD))
    }

    /**
     * Helper method for creating leaves
     * copied over from Vanilla
     */
    private fun leaves(arg: SoundType): LeavesBlock {
        return LeavesBlock(
            BlockBehaviour.Properties.of(Material.LEAVES).strength(0.2f).randomTicks().sound(arg).noOcclusion()
                .isValidSpawn { arg: BlockState, arg2: BlockGetter, arg3: BlockPos, arg4: EntityType<*> ->
                    ocelotOrParrot(arg, arg2, arg3, arg4)
                }.isSuffocating { arg: BlockState, arg2: BlockGetter, arg3: BlockPos ->
                    never(arg, arg2, arg3)
                }.isViewBlocking { arg: BlockState, arg2: BlockGetter, arg3: BlockPos ->
                    never(arg, arg2, arg3)
                })
    }

    private fun always(arg: BlockState, arg2: BlockGetter, arg3: BlockPos): Boolean {
        return true
    }

    private fun never(arg: BlockState, arg2: BlockGetter, arg3: BlockPos): Boolean {
        return false
    }

    private fun ocelotOrParrot(arg: BlockState, arg2: BlockGetter, arg3: BlockPos, arg4: EntityType<*>): Boolean {
        return arg4 === EntityType.OCELOT || arg4 === EntityType.PARROT
    }
}