package com.cablemc.pokemoncobbled.common.world.feature

import com.cablemc.pokemoncobbled.common.CobbledBlocks
import com.mojang.serialization.Codec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.WorldGenLevel
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration
import java.util.*

class ApricornTreeFeature(
    codec: Codec<NoneFeatureConfiguration>
) : Feature<NoneFeatureConfiguration>(codec) {

    override fun place(context: FeaturePlaceContext<NoneFeatureConfiguration>) : Boolean {
        val worldGenLevel: WorldGenLevel = context.level()
        val random: Random = context.random()
        val origin: BlockPos = context.origin()

        // Create chunk
        val logState = CobbledBlocks.APRICORN_LOG.get().defaultBlockState();
        for(y in 0..4) {
            val logPos = origin.relative(Direction.UP, y)
            worldGenLevel.level.setBlock(logPos, logState, 19)
        }

        // Decorate with leaves


        return true;
    }

}