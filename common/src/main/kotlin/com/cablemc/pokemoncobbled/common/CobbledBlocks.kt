package com.cablemc.pokemoncobbled.common

import com.cablemc.pokemoncobbled.common.item.ApricornItem
import com.cablemc.pokemoncobbled.common.world.level.block.ApricornBlock
import com.cablemc.pokemoncobbled.common.world.level.block.ApricornSaplingBlock
import com.cablemc.pokemoncobbled.common.world.level.block.HealingMachineBlock
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.block.*
import net.minecraft.entity.EntityType
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.registry.Registry
import net.minecraft.world.BlockView
import java.util.function.Supplier

object CobbledBlocks {
    private val blockRegister = DeferredRegister.create(PokemonCobbled.MODID, Registry.BLOCK_KEY)

    private fun <T : Block> queue(name: String, block: Supplier<T>) = blockRegister.register(name, block)

//    val APRICORN_WOOD_TYPE = WoodType.register(ExtendedWoodType("apricorn"))

    val APRICORN_LOG = queue("apricorn_log") { log(MapColor.DIRT_BROWN, MapColor.BROWN) }
    val STRIPPED_APRICORN_LOG = queue("stripped_apricorn_log") { log(MapColor.DIRT_BROWN, MapColor.DIRT_BROWN) }
    val APRICORN_WOOD = queue("apricorn_wood") { log(MapColor.DIRT_BROWN, MapColor.DIRT_BROWN) }
    val STRIPPED_APRICORN_WOOD = queue("stripped_apricorn_wood") { log(MapColor.DIRT_BROWN, MapColor.DIRT_BROWN) }
    val APRICORN_PLANKS = queue("apricorn_planks") { Block(AbstractBlock.Settings.of(Material.WOOD, MapColor.DIRT_BROWN).strength(2.0f, 3.0f).sounds(BlockSoundGroup.WOOD)) }
    val APRICORN_LEAVES = queue("apricorn_leaves") { leaves(BlockSoundGroup.GRASS) }
    val APRICORN_FENCE = queue("apricorn_fence") { FenceBlock(AbstractBlock.Settings.of(Material.WOOD, APRICORN_PLANKS.get().defaultMapColor).strength(2.0f, 3.0f).sounds(BlockSoundGroup.WOOD)) }
    val APRICORN_FENCE_GATE = queue("apricorn_fence_gate") { FenceGateBlock(AbstractBlock.Settings.of(Material.WOOD, APRICORN_PLANKS.get().defaultMapColor).strength(2.0f, 3.0f).sounds(BlockSoundGroup.WOOD)) }
    val APRICORN_BUTTON = queue("apricorn_button") { WoodenButtonBlock(AbstractBlock.Settings.of(Material.DECORATION).noCollision().strength(0.5f).sounds(BlockSoundGroup.WOOD)) }
    val APRICORN_PRESSURE_PLATE = queue("apricorn_pressure_plate") { PressurePlateBlock(PressurePlateBlock.ActivationRule.EVERYTHING, AbstractBlock.Settings.of(Material.WOOD, APRICORN_PLANKS.get().defaultMapColor).noCollision().strength(0.5f).sounds(BlockSoundGroup.WOOD)) }
    //val APRICORN_SIGN = queue("apricorn_sign") { StandingSignBlock(AbstractBlock.Settings.of(Material.WOOD).noCollission().strength(1.0f).sounds(BlockSoundGroup.WOOD), APRICORN_WOOD_TYPE) }
    //val APRICORN_WALL_SIGN = queue("apricorn_wall_sign") { WallSignBlock(AbstractBlock.Settings.of(Material.WOOD).noCollission().strength(1.0f).sounds(BlockSoundGroup.WOOD).dropsLike(APRICORN_SIGN), APRICORN_WOOD_TYPE) }
    val APRICORN_SLAB = queue("apricorn_slab") { SlabBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.OAK_TAN).strength(2.0f, 3.0f).sounds(BlockSoundGroup.WOOD)) }
    val APRICORN_STAIRS = queue("apricorn_stairs") { StairsBlock(APRICORN_PLANKS.get().defaultState, AbstractBlock.Settings.copy(APRICORN_PLANKS.get())) }
    val APRICORN_DOOR = queue("apricorn_door") { DoorBlock(AbstractBlock.Settings.of(Material.WOOD, APRICORN_PLANKS.get().defaultMapColor).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque()) }
    val APRICORN_TRAPDOOR = queue("apricorn_trapdoor") { TrapdoorBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.OAK_TAN).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque().allowsSpawning(CobbledBlocks::never)) }

    private val PLANT_PROPERTIES = AbstractBlock.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.GRASS)
    val BLACK_APRICORN_SAPLING = queue("black_apricorn_sapling") { ApricornSaplingBlock(PLANT_PROPERTIES, "black") }
    val BLUE_APRICORN_SAPLING = queue("blue_apricorn_sapling") { ApricornSaplingBlock(PLANT_PROPERTIES, "blue") }
    val GREEN_APRICORN_SAPLING = queue("green_apricorn_sapling") { ApricornSaplingBlock(PLANT_PROPERTIES, "green") }
    val PINK_APRICORN_SAPLING = queue("pink_apricorn_sapling") { ApricornSaplingBlock(PLANT_PROPERTIES, "pink") }
    val RED_APRICORN_SAPLING = queue("red_apricorn_sapling") { ApricornSaplingBlock(PLANT_PROPERTIES, "red") }
    val WHITE_APRICORN_SAPLING = queue("white_apricorn_sapling") { ApricornSaplingBlock(PLANT_PROPERTIES, "white") }
    val YELLOW_APRICORN_SAPLING = queue("yellow_apricorn_sapling") { ApricornSaplingBlock(PLANT_PROPERTIES, "yellow") }

    val BLACK_APRICORN = registerApricornBlock("black_apricorn") { CobbledItems.BLACK_APRICORN.get() }
    val BLUE_APRICORN = registerApricornBlock("blue_apricorn") { CobbledItems.BLUE_APRICORN.get() }
    val GREEN_APRICORN = registerApricornBlock("green_apricorn") { CobbledItems.GREEN_APRICORN.get() }
    val PINK_APRICORN = registerApricornBlock("pink_apricorn") { CobbledItems.PINK_APRICORN.get() }
    val RED_APRICORN = registerApricornBlock("red_apricorn") { CobbledItems.RED_APRICORN.get() }
    val WHITE_APRICORN = registerApricornBlock("white_apricorn") { CobbledItems.WHITE_APRICORN.get() }
    val YELLOW_APRICORN = registerApricornBlock("yellow_apricorn") { CobbledItems.YELLOW_APRICORN.get() }

    val HEALING_MACHINE = queue("healing_machine") { HealingMachineBlock(AbstractBlock.Settings.of(Material.METAL, MapColor.IRON_GRAY).sounds(BlockSoundGroup.METAL).strength(2f).nonOpaque()) }

    fun register() {
        blockRegister.register()
    }

    private fun registerApricornBlock(id: String, apricornSupplier: Supplier<ApricornItem>): RegistrySupplier<ApricornBlock> {
        return queue(id) { ApricornBlock(AbstractBlock.Settings.of(Material.PLANT).ticksRandomly().strength(0.2f, 3.0f).sounds(BlockSoundGroup.WOOD).nonOpaque(), apricornSupplier) }
    }

    /**
     * Helper method for creating logs
     * copied over from Vanilla
     */
    private fun log(arg: MapColor, arg2: MapColor): PillarBlock {
        return PillarBlock(AbstractBlock.Settings.of(Material.WOOD) { arg3: BlockState ->
            if (arg3.get(PillarBlock.AXIS) === Direction.Axis.Y) arg else arg2
        }.strength(2.0f).sounds(BlockSoundGroup.WOOD))
    }

    /**
     * Helper method for creating leaves
     * copied over from Vanilla
     */
    private fun leaves(sound: BlockSoundGroup): LeavesBlock {
        return LeavesBlock(
            AbstractBlock.Settings.of(Material.LEAVES).strength(0.2f).ticksRandomly().sounds(sound).nonOpaque()
                .allowsSpawning { arg: BlockState, arg2: BlockView, arg3: BlockPos, arg4: EntityType<*> -> ocelotOrParrot(arg, arg2, arg3, arg4) }
                .suffocates { arg: BlockState, arg2: BlockView, arg3: BlockPos -> false }
                .blockVision { arg: BlockState, arg2: BlockView, arg3: BlockPos -> false })
    }

    private fun ocelotOrParrot(arg: BlockState, arg2: BlockView, arg3: BlockPos, arg4: EntityType<*>): Boolean {
        return arg4 === EntityType.OCELOT || arg4 === EntityType.PARROT
    }

    // Short hands for predicate arguments
    private fun always(arg: BlockState, arg2: BlockView, arg3: BlockPos): Boolean {
        return true
    }

    private fun never(arg: BlockState, arg2: BlockView, arg3: BlockPos): Boolean {
        return false
    }

    private fun never(arg: BlockState, arg2: BlockView, arg3: BlockPos, arg4: EntityType<*>): Boolean {
        return false;
    }

    private fun always(arg: BlockState, arg2: BlockView, arg3: BlockPos, arg4: EntityType<*>): Boolean {
        return true;
    }
}