package com.cablemc.pokemoncobbled.common

import com.cablemc.pokemoncobbled.common.api.pokeball.PokeBalls
import com.cablemc.pokemoncobbled.common.item.ApricornItem
import com.cablemc.pokemoncobbled.common.item.PokeBallItem
import com.cablemc.pokemoncobbled.common.pokeball.PokeBall
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.block.Block
import net.minecraft.item.AliasedBlockItem
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.util.registry.Registry
import java.util.function.Supplier

object CobbledItems {
    
    private val itemRegister = DeferredRegister.create(PokemonCobbled.MODID, Registry.ITEM_KEY)
    private fun <T : Item> queue(name: String, item: Supplier<T>) = itemRegister.register(name, item)

    val POKE_BALL = queue("poke_ball") { PokeBallItem(PokeBalls.POKE_BALL) }
    val GREAT_BALL = queue("great_ball") { PokeBallItem(PokeBalls.GREAT_BALL) }
    val ULTRA_BALL = queue("ultra_ball") { PokeBallItem(PokeBalls.ULTRA_BALL) }
    val MASTER_BALL = queue("master_ball") { PokeBallItem(PokeBalls.MASTER_BALL) }

    val ballMap = mutableMapOf<PokeBall, RegistrySupplier<PokeBallItem>>()

    val BLACK_APRICORN = queue("black_apricorn") { ApricornItem() }
    val BLUE_APRICORN = queue("blue_apricorn") { ApricornItem() }
    val GREEN_APRICORN = queue("green_apricorn") { ApricornItem() }
    val PINK_APRICORN = queue("pink_apricorn") { ApricornItem() }
    val RED_APRICORN = queue("red_apricorn") { ApricornItem() }
    val WHITE_APRICORN = queue("white_apricorn") { ApricornItem() }
    val YELLOW_APRICORN = queue("yellow_apricorn") { ApricornItem() }

    val BLACK_APRICORN_SEED = queue("black_apricorn_seed") { itemNameBlockItem(CobbledBlocks.BLACK_APRICORN_SAPLING.get(), ItemGroup.MISC) }
    val BLUE_APRICORN_SEED = queue("blue_apricorn_seed") { itemNameBlockItem(CobbledBlocks.BLUE_APRICORN_SAPLING.get(), ItemGroup.MISC) }
    val GREEN_APRICORN_SEED = queue("green_apricorn_seed") { itemNameBlockItem(CobbledBlocks.GREEN_APRICORN_SAPLING.get(), ItemGroup.MISC) }
    val PINK_APRICORN_SEED = queue("pink_apricorn_seed") { itemNameBlockItem(CobbledBlocks.PINK_APRICORN_SAPLING.get(), ItemGroup.MISC) }
    val RED_APRICORN_SEED = queue("red_apricorn_seed") { itemNameBlockItem(CobbledBlocks.RED_APRICORN_SAPLING.get(), ItemGroup.MISC) }
    val WHITE_APRICORN_SEED = queue("white_apricorn_seed") { itemNameBlockItem(CobbledBlocks.WHITE_APRICORN_SAPLING.get(), ItemGroup.MISC) }
    val YELLOW_APRICORN_SEED = queue("yellow_apricorn_seed") { itemNameBlockItem(CobbledBlocks.YELLOW_APRICORN_SAPLING.get(), ItemGroup.MISC) }

    val APRICORN_LOG = queue("apricorn_log") { blockItem(CobbledBlocks.APRICORN_LOG.get(), ItemGroup.BUILDING_BLOCKS) }
    val STRIPPED_APRICORN_LOG = queue("stripped_apricorn_log") { blockItem(CobbledBlocks.STRIPPED_APRICORN_LOG.get(), ItemGroup.BUILDING_BLOCKS) }
    val APRICORN_WOOD = queue("apricorn_wood") { blockItem(CobbledBlocks.APRICORN_WOOD.get(), ItemGroup.BUILDING_BLOCKS) }
    val STRIPPED_APRICORN_WOOD = queue("stripped_apricorn_wood") { blockItem(CobbledBlocks.STRIPPED_APRICORN_WOOD.get(), ItemGroup.BUILDING_BLOCKS) }
    val APRICORN_PLANKS = queue("apricorn_planks") { blockItem(CobbledBlocks.APRICORN_PLANKS.get(), ItemGroup.BUILDING_BLOCKS) }
    val APRICORN_LEAVES = queue("apricorn_leaves") { blockItem(CobbledBlocks.APRICORN_LEAVES.get(), ItemGroup.BUILDING_BLOCKS) }

    val APRICORN_DOOR = queue("apricorn_door") { blockItem(CobbledBlocks.APRICORN_DOOR.get(), ItemGroup.REDSTONE) }
    val APRICORN_TRAPDOOR = queue("apricorn_trapdoor") { blockItem(CobbledBlocks.APRICORN_TRAPDOOR.get(), ItemGroup.REDSTONE) }
    val APRICORN_FENCE = queue("apricorn_fence") { blockItem(CobbledBlocks.APRICORN_FENCE.get(), ItemGroup.DECORATIONS) }
    val APRICORN_FENCE_GATE = queue("apricorn_fence_gate") { blockItem(CobbledBlocks.APRICORN_FENCE_GATE.get(), ItemGroup.REDSTONE) }
    val APRICORN_BUTTON = queue("apricorn_button") { blockItem(CobbledBlocks.APRICORN_BUTTON.get(), ItemGroup.REDSTONE) }
    val APRICORN_PRESSURE_PLATE = queue("apricorn_pressure_plate") { blockItem(CobbledBlocks.APRICORN_PRESSURE_PLATE.get(), ItemGroup.REDSTONE) }
    //val APRICORN_SIGN = queue("apricorn_sign", SignItem(Item.Properties().stacksTo(16).tab(CreativeModeTab.TAB_DECORATIONS), CobbledBlocks.APRICORN_SIGN, CobbledBlocks.APRICORN_WALL_SIGN))
    val APRICORN_SLAB = queue("apricorn_slab") { blockItem(CobbledBlocks.APRICORN_SLAB.get(), ItemGroup.BUILDING_BLOCKS) }
    val APRICORN_STAIRS = queue("apricorn_stairs") { blockItem(CobbledBlocks.APRICORN_STAIRS.get(), ItemGroup.BUILDING_BLOCKS) }

    val HEALING_MACHINE = queue("healing_machine") { blockItem(CobbledBlocks.HEALING_MACHINE.get(), ItemGroup.REDSTONE) }

    private fun blockItem(block: Block, tab: ItemGroup) : BlockItem {
        return BlockItem(block, Item.Settings().group(tab))
    }

    private fun itemNameBlockItem(block: Block, tab: ItemGroup) : BlockItem {
        return AliasedBlockItem(block, Item.Settings().group(tab))
    }

    fun register() {
        itemRegister.register()
        ballMap[PokeBalls.POKE_BALL] = POKE_BALL
        ballMap[PokeBalls.GREAT_BALL] = GREAT_BALL
        ballMap[PokeBalls.ULTRA_BALL] = ULTRA_BALL
        ballMap[PokeBalls.MASTER_BALL] = MASTER_BALL
    }
}