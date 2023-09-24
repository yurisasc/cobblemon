package com.cobblemon.mod.common.api.pokemon.species.internal

import com.cobblemon.mod.common.api.pokemon.egg.EggGroup
import com.cobblemon.mod.common.api.pokemon.species.Species
import com.cobblemon.mod.common.util.codec.setCodec
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import java.util.EnumSet

/**
 * Intermediate for [Species] data.
 */
internal data class EggData(
    var eggCycles: Int,
    var eggGroups: EnumSet<EggGroup>,
) {

    companion object {

        val CODEC: Codec<EggData> = RecordCodecBuilder.create { builder ->
            builder.group(
                Codec.INT.fieldOf("eggCycles").forGetter(EggData::eggCycles),
                setCodec(EggGroup.CODEC).fieldOf("eggGroups").xmap({ set -> EnumSet.copyOf(set) }, { enumSet -> enumSet }).forGetter { eggData -> eggData.eggGroups }
            ).apply(builder, ::EggData)
        }

    }

}
