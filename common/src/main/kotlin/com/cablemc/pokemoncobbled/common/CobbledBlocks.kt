package com.cablemc.pokemoncobbled.common

import com.cablemc.pokemoncobbled.common.item.ApricornItem
import com.cablemc.pokemoncobbled.common.util.ExtendedWoodType
import com.cablemc.pokemoncobbled.common.world.level.block.ApricornBlock
import com.cablemc.pokemoncobbled.common.world.level.block.ApricornSaplingBlock
import dev.architectury.hooks.item.tool.AxeItemHooks
import dev.architectury.registry.registries.DeferredRegister
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Registry
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.WoodType
import net.minecraft.world.level.material.Material
import net.minecraft.world.level.material.MaterialColor
import java.util.function.Supplier

object CobbledBlocks {
    private val blockRegister = DeferredRegister.create(PokemonCobbled.MODID, Registry.BLOCK_REGISTRY)

    private fun <T : Block> queue(name: String, block: T) = blockRegister.register(name) { block }

    val APRICORN_WOOD_TYPE = WoodType.register(ExtendedWoodType("apricorn"))

    val APRICORN_LOG = registerBlock("apricorn_log", log(MaterialColor.PODZOL, MaterialColor.COLOR_BROWN))
    val STRIPPED_APRICORN_LOG = registerBlock("stripped_apricorn_log", log(MaterialColor.PODZOL, MaterialColor.PODZOL))
    val APRICORN_WOOD = registerBlock("apricorn_wood", log(MaterialColor.PODZOL, MaterialColor.PODZOL))
    val STRIPPED_APRICORN_WOOD = registerBlock("stripped_apricorn_wood", log(MaterialColor.PODZOL, MaterialColor.PODZOL))
    val APRICORN_PLANKS = registerBlock("apricorn_planks", Block(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.PODZOL).strength(2.0f, 3.0f).sound(SoundType.WOOD)))
    val APRICORN_LEAVES = registerBlock("apricorn_leaves", leaves(SoundType.GRASS))
    val APRICORN_FENCE = registerBlock("apricorn_fence", FenceBlock(BlockBehaviour.Properties.of(Material.WOOD, APRICORN_PLANKS.defaultMaterialColor()).strength(2.0f, 3.0f).sound(SoundType.WOOD)))
    val APRICORN_FENCE_GATE = registerBlock("apricorn_fence_gate", FenceGateBlock(BlockBehaviour.Properties.of(Material.WOOD, APRICORN_PLANKS.defaultMaterialColor()).strength(2.0f, 3.0f).sound(SoundType.WOOD)))
    val APRICORN_BUTTON = registerBlock("apricorn_button", WoodButtonBlock(BlockBehaviour.Properties.of(Material.DECORATION).noCollission().strength(0.5f).sound(SoundType.WOOD)))
    val APRICORN_PRESSURE_PLATE = registerBlock("apricorn_pressure_plate", PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, BlockBehaviour.Properties.of(Material.WOOD, APRICORN_PLANKS.defaultMaterialColor()).noCollission().strength(0.5f).sound(SoundType.WOOD)))
    val APRICORN_SIGN = registerBlock("apricorn_sign", StandingSignBlock(BlockBehaviour.Properties.of(Material.WOOD).noCollission().strength(1.0f).sound(SoundType.WOOD), APRICORN_WOOD_TYPE))
    val APRICORN_WALL_SIGN = registerBlock("apricorn_wall_sign", WallSignBlock(BlockBehaviour.Properties.of(Material.WOOD).noCollission().strength(1.0f).sound(SoundType.WOOD).dropsLike(APRICORN_SIGN), APRICORN_WOOD_TYPE))
    val APRICORN_SLAB = registerBlock("apricorn_slab", SlabBlock(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(2.0f, 3.0f).sound(SoundType.WOOD)))
    val APRICORN_STAIRS = registerBlock("apricorn_stairs", StairBlock(APRICORN_PLANKS.defaultBlockState(), BlockBehaviour.Properties.copy(APRICORN_PLANKS)))
    val APRICORN_DOOR = registerBlock("apricorn_door", DoorBlock(BlockBehaviour.Properties.of(Material.WOOD, APRICORN_PLANKS.defaultMaterialColor()).strength(3.0F).sound(SoundType.WOOD).noOcclusion()))
    val APRICORN_TRAPDOOR = registerBlock("apricorn_trapdoor", TrapDoorBlock(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(3.0F).sound(SoundType.WOOD).noOcclusion().isValidSpawn(CobbledBlocks::never)))

    private val PLANT_PROPERTIES = BlockBehaviour.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS)
    val BLACK_APRICORN_SAPLING = registerBlock("black_apricorn_sapling", ApricornSaplingBlock(PLANT_PROPERTIES, "black"))
    val BLUE_APRICORN_SAPLING = registerBlock("blue_apricorn_sapling", ApricornSaplingBlock(PLANT_PROPERTIES, "blue"))
    val GREEN_APRICORN_SAPLING = registerBlock("green_apricorn_sapling", ApricornSaplingBlock(PLANT_PROPERTIES, "green"))
    val PINK_APRICORN_SAPLING = registerBlock("pink_apricorn_sapling", ApricornSaplingBlock(PLANT_PROPERTIES, "pink"))
    val RED_APRICORN_SAPLING = registerBlock("red_apricorn_sapling", ApricornSaplingBlock(PLANT_PROPERTIES, "red"))
    val WHITE_APRICORN_SAPLING = registerBlock("white_apricorn_sapling", ApricornSaplingBlock(PLANT_PROPERTIES, "white"))
    val YELLOW_APRICORN_SAPLING = registerBlock("yellow_apricorn_sapling", ApricornSaplingBlock(PLANT_PROPERTIES, "yellow"))

    val BLACK_APRICORN = registerApricornBlock("black_apricorn") { CobbledItems.BLACK_APRICORN.get() }
    val BLUE_APRICORN = registerApricornBlock("blue_apricorn") { CobbledItems.BLUE_APRICORN.get() }
    val GREEN_APRICORN = registerApricornBlock("green_apricorn") { CobbledItems.GREEN_APRICORN.get() }
    val PINK_APRICORN = registerApricornBlock("pink_apricorn") { CobbledItems.PINK_APRICORN.get() }
    val RED_APRICORN = registerApricornBlock("red_apricorn") { CobbledItems.RED_APRICORN.get() }
    val WHITE_APRICORN = registerApricornBlock("white_apricorn") { CobbledItems.WHITE_APRICORN.get() }
    val YELLOW_APRICORN = registerApricornBlock("yellow_apricorn") { CobbledItems.YELLOW_APRICORN.get() }

    fun register() {
        blockRegister.register()

        AxeItemHooks.addStrippable(APRICORN_LOG, STRIPPED_APRICORN_LOG)
        AxeItemHooks.addStrippable(APRICORN_WOOD, STRIPPED_APRICORN_WOOD)
    }

    private fun registerApricornBlock(id: String, apricornSupplier: Supplier<ApricornItem>): Block {
        return registerBlock(id, ApricornBlock(BlockBehaviour.Properties.of(Material.PLANT).randomTicks().strength(0.2f, 3.0f).sound(SoundType.WOOD).noOcclusion(), apricornSupplier))
    }

    private fun registerBlock(id: String, block: Block): Block {
        blockRegister.register(id) { block }
        return block;
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
                .isValidSpawn { arg: BlockState, arg2: BlockGetter, arg3: BlockPos, arg4: EntityType<*> -> ocelotOrParrot(arg, arg2, arg3, arg4) }
                .isSuffocating { arg: BlockState, arg2: BlockGetter, arg3: BlockPos -> false }
                .isViewBlocking { arg: BlockState, arg2: BlockGetter, arg3: BlockPos -> false })
    }

    private fun ocelotOrParrot(arg: BlockState, arg2: BlockGetter, arg3: BlockPos, arg4: EntityType<*>): Boolean {
        return arg4 === EntityType.OCELOT || arg4 === EntityType.PARROT
    }

    // Short hands for predicate arguments
    private fun always(arg: BlockState, arg2: BlockGetter, arg3: BlockPos): Boolean {
        return true
    }

    private fun never(arg: BlockState, arg2: BlockGetter, arg3: BlockPos): Boolean {
        return false
    }

    private fun never(arg: BlockState, arg2: BlockGetter, arg3: BlockPos, arg4: EntityType<*>): Boolean {
        return false;
    }

    private fun always(arg: BlockState, arg2: BlockGetter, arg3: BlockPos, arg4: EntityType<*>): Boolean {
        return true;
    }
}