package com.cablemc.pokemod.common

import com.cablemc.pokemod.common.util.pokemodResource
import net.minecraft.tag.TagKey
import net.minecraft.util.registry.Registry

/**
 * A collection of the Pokemod [TagKey]s related to the [Registry.BLOCK_KEY].
 *
 * @author Licious
 * @since October 29th, 2022
 */
object PokemodBlockTags {

    val APRICORN_LEAVES = createTag("apricorn_leaves")
    val APRICORNS = createTag("apricorns")
    val DRIP_STONE_REPLACEABLES = createTag("drip_stone_replaceables")
    val FENCE_GATES = createTag("fence_gates")
    val FENCES = createTag("fences")
    val LEAVES = createTag("leaves")
    val LOGS = createTag("logs")
    val LOGS_THAT_BURN = createTag("logs_that_burn")
    val PLANKS = createTag("planks")
    val SAPLINGS = createTag("saplings")
    val STANDING_SIGNS = createTag("standing_signs")
    val WALL_SIGNS = createTag("wall_signs")
    val WOODEN_BUTTONS = createTag("wooden_buttons")
    val WOODEN_FENCES = createTag("wooden_fences")
    val WOODEN_PRESSURE_PLATES = createTag("wooden_pressure_plates")
    val WOODEN_SLABS = createTag("wooden_slabs")
    val WOODEN_STAIRS = createTag("wooden_stairs")

    private fun createTag(name: String) = TagKey.of(Registry.BLOCK_KEY, pokemodResource(name))

}