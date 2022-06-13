package com.cablemc.pokemoncobbled.common

import com.cablemc.pokemoncobbled.common.api.blocks.EvolutionStoneOre
import com.cablemc.pokemoncobbled.common.api.blocks.EvolutionStoneOre.Companion.DEEPSLATE_PROPERTIES
import com.cablemc.pokemoncobbled.common.api.blocks.EvolutionStoneOre.Companion.NORMAL_PROPERTIES
import com.cablemc.pokemoncobbled.common.item.ApricornItem
import com.cablemc.pokemoncobbled.common.world.level.block.ApricornBlock
import com.cablemc.pokemoncobbled.common.world.level.block.ApricornSaplingBlock
import com.cablemc.pokemoncobbled.common.world.level.block.HealingMachineBlock
import com.cablemc.pokemoncobbled.common.world.level.block.PCBlock
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.block.*
import net.minecraft.entity.EntityType
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.math.Direction
import net.minecraft.util.registry.Registry
import java.util.function.Supplier

object CobbledBlocks {
    private val blockRegister = DeferredRegister.create(PokemonCobbled.MODID, Registry.BLOCK_KEY)
    private fun <T : Block> queue(name: String, block: Supplier<T>) = blockRegister.register(name, block)

    /**
     * Evolution Ores
     */

    val DAWN_STONE_ORE = queue("dawn_stone_ore") { EvolutionStoneOre(NORMAL_PROPERTIES) }
    val DUSK_STONE_ORE = queue("dusk_stone_ore") { EvolutionStoneOre(NORMAL_PROPERTIES) }
    val FIRE_STONE_ORE = queue("fire_stone_ore") { EvolutionStoneOre(NORMAL_PROPERTIES) }
    val ICE_STONE_ORE = queue("ice_stone_ore") { EvolutionStoneOre(NORMAL_PROPERTIES) }
    val LEAF_STONE_ORE = queue("leaf_stone_ore") { EvolutionStoneOre(NORMAL_PROPERTIES) }
    val MOON_STONE_ORE = queue("moon_stone_ore") { EvolutionStoneOre(NORMAL_PROPERTIES) }
    val DRIPSTONE_MOON_STONE_ORE = queue("dripstone_moon_stone_ore") { EvolutionStoneOre(NORMAL_PROPERTIES) }
    val SHINY_STONE_ORE = queue("shiny_stone_ore") { EvolutionStoneOre(NORMAL_PROPERTIES) }
    val SUN_STONE_ORE = queue("sun_stone_ore") { EvolutionStoneOre(NORMAL_PROPERTIES) }
    val THUNDER_STONE_ORE = queue("thunder_stone_ore") { EvolutionStoneOre(NORMAL_PROPERTIES) }
    val WATER_STONE_ORE = queue("water_stone_ore") { EvolutionStoneOre(NORMAL_PROPERTIES) }

    /**
     * Deepslate separator
     */

    val DEEPSLATE_DAWN_STONE_ORE = queue("deepslate_dawn_stone_ore") { EvolutionStoneOre(DEEPSLATE_PROPERTIES) }
    val DEEPSLATE_DUSK_STONE_ORE = queue("deepslate_dusk_stone_ore") { EvolutionStoneOre(DEEPSLATE_PROPERTIES) }
    val DEEPSLATE_FIRE_STONE_ORE = queue("deepslate_fire_stone_ore") { EvolutionStoneOre(DEEPSLATE_PROPERTIES) }
    val DEEPSLATE_ICE_STONE_ORE = queue("deepslate_ice_stone_ore") { EvolutionStoneOre(DEEPSLATE_PROPERTIES) }
    val DEEPSLATE_LEAF_STONE_ORE = queue("deepslate_leaf_stone_ore") { EvolutionStoneOre(DEEPSLATE_PROPERTIES) }
    val DEEPSLATE_MOON_STONE_ORE = queue("deepslate_moon_stone_ore") { EvolutionStoneOre(DEEPSLATE_PROPERTIES) }
    val DEEPSLATE_SHINY_STONE_ORE = queue("deepslate_shiny_stone_ore") { EvolutionStoneOre(DEEPSLATE_PROPERTIES) }
    val DEEPSLATE_SUN_STONE_ORE = queue("deepslate_sun_stone_ore") { EvolutionStoneOre(DEEPSLATE_PROPERTIES) }
    val DEEPSLATE_THUNDER_STONE_ORE = queue("deepslate_thunder_stone_ore") { EvolutionStoneOre(DEEPSLATE_PROPERTIES) }
    val DEEPSLATE_WATER_STONE_ORE = queue("deepslate_water_stone_ore") { EvolutionStoneOre(DEEPSLATE_PROPERTIES) }

    /**
     * Apricorns
     */

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
    // Tag was removed be sure to add it back when implemented
    //val APRICORN_SIGN = queue("apricorn_sign") { StandingSignBlock(AbstractBlock.Settings.of(Material.WOOD).noCollission().strength(1.0f).sounds(BlockSoundGroup.WOOD), APRICORN_WOOD_TYPE) }
    //val APRICORN_WALL_SIGN = queue("apricorn_wall_sign") { WallSignBlock(AbstractBlock.Settings.of(Material.WOOD).noCollission().strength(1.0f).sounds(BlockSoundGroup.WOOD).dropsLike(APRICORN_SIGN), APRICORN_WOOD_TYPE) }
    val APRICORN_SLAB = queue("apricorn_slab") { SlabBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.OAK_TAN).strength(2.0f, 3.0f).sounds(BlockSoundGroup.WOOD)) }
    val APRICORN_STAIRS = queue("apricorn_stairs") { StairsBlock(APRICORN_PLANKS.get().defaultState, AbstractBlock.Settings.copy(APRICORN_PLANKS.get())) }
    val APRICORN_DOOR = queue("apricorn_door") { DoorBlock(AbstractBlock.Settings.of(Material.WOOD, APRICORN_PLANKS.get().defaultMapColor).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque()) }
    val APRICORN_TRAPDOOR = queue("apricorn_trapdoor") { TrapdoorBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.OAK_TAN).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque().allowsSpawning { _, _, _, _ -> false }) }

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
    val PC = queue("pc") { PCBlock(AbstractBlock.Settings.of(Material.METAL, MapColor.IRON_GRAY).sounds(BlockSoundGroup.METAL).strength(2F).nonOpaque()) }

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
                .allowsSpawning { _, _, _, type -> type === EntityType.OCELOT || type === EntityType.PARROT }
                .suffocates { _, _, _ -> false }
                .blockVision { _, _, _ -> false })
    }
}