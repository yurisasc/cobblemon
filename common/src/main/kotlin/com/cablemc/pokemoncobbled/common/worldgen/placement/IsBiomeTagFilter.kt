package com.cablemc.pokemoncobbled.common.worldgen.placement

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.tags.TagKey
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.levelgen.placement.PlacementContext
import net.minecraft.world.level.levelgen.placement.PlacementFilter
import net.minecraft.world.level.levelgen.placement.PlacementModifierType
import java.util.Random

/**
 * A world generation placement filter which makes placing conditional on
 * the biome being a part of the given [TagKey].
 *
 * Largely helped by BYG code which had the same idea as us but actually
 * knew how to do it and saved some time.
 *
 * @author Hiroku
 * @since March 25th, 2022
 */
class IsBiomeTagFilter(private val tag: TagKey<Biome>) : PlacementFilter() {
    override fun type(): PlacementModifierType<*> = CobbledPlacementTypes.IS_BIOME_TAG_FILTER
    override fun shouldPlace(ctx: PlacementContext, r: Random, pos: BlockPos) = ctx.level.getBiome(pos).`is`(tag)

    companion object {
        val CODEC: Codec<IsBiomeTagFilter> = RecordCodecBuilder.create { builder ->
            builder
                .group(
                    TagKey.codec(Registry.BIOME_REGISTRY)
                        .fieldOf("valid_biome")
                        .forGetter { it.tag }
                )
                .apply(builder) { IsBiomeTagFilter(it) }
        }
    }
}