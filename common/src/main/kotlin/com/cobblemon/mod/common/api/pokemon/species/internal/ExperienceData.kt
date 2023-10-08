package com.cobblemon.mod.common.api.pokemon.species.internal

import com.cobblemon.mod.common.api.pokemon.experience.ExperienceGroup
import com.cobblemon.mod.common.api.pokemon.species.Species
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder

/**
 * Intermediate for [Species] data.
 */
internal data class ExperienceData(
    var baseExperienceYield: Int,
    var experienceGroup: ExperienceGroup
) {

    companion object {

        val MAP_CODEC: MapCodec<ExperienceData> = RecordCodecBuilder.mapCodec { builder ->
            builder.group(
                Codec.INT.fieldOf("baseExperienceYield").forGetter(ExperienceData::baseExperienceYield),
                ExperienceGroup.CODEC.fieldOf("experienceGroup").forGetter(ExperienceData::experienceGroup),
            ).apply(builder, ::ExperienceData)
        }

    }

}