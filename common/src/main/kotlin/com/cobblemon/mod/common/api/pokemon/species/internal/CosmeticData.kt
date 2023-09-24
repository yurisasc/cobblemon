package com.cobblemon.mod.common.api.pokemon.species.internal

import com.cobblemon.mod.common.api.pokemon.species.Species
import com.cobblemon.mod.common.util.codec.ExtraCodecs
import com.cobblemon.mod.common.util.codec.setCodec
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.entity.EntityDimensions

/**
 * Intermediate for [Species] data.
 */
internal data class CosmeticData(
    var baseScale: Float,
    var hitbox: EntityDimensions,
    var weight: Float,
    var height: Float,
    var aspects: Set<String>,
    var standingEyeHeight: Float,
    var swimmingEyeHeight: Float,
    var flyingEyeHeight: Float,
) {

    companion object {

        val CODEC: Codec<CosmeticData> = RecordCodecBuilder.create { builder ->
            builder.group(
                Codec.FLOAT.fieldOf("baseScale").forGetter(CosmeticData::baseScale),
                ExtraCodecs.ENTITY_DIMENSIONS.fieldOf("hitbox").forGetter(CosmeticData::hitbox),
                Codec.FLOAT.fieldOf("weight").forGetter(CosmeticData::weight),
                Codec.FLOAT.fieldOf("height").forGetter(CosmeticData::height),
                setCodec(Codec.STRING).fieldOf("aspects").forGetter(CosmeticData::aspects),
                Codec.FLOAT.optionalFieldOf("standingEyeHeight", VANILLA_DEFAULT_EYE_HEIGHT).forGetter(CosmeticData::standingEyeHeight),
                Codec.FLOAT.optionalFieldOf("swimmingEyeHeight", VANILLA_DEFAULT_EYE_HEIGHT).forGetter(CosmeticData::swimmingEyeHeight),
                Codec.FLOAT.optionalFieldOf("flyingEyeHeight", VANILLA_DEFAULT_EYE_HEIGHT).forGetter(CosmeticData::flyingEyeHeight),
            ).apply(builder, ::CosmeticData)
        }

        private const val VANILLA_DEFAULT_EYE_HEIGHT = 0.85F

    }

}
