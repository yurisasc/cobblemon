package com.cobblemon.mod.common.world.foliage

import com.cobblemon.mod.common.util.cobblemonResource
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.util.math.intprovider.IntProvider
import net.minecraft.util.math.random.Random
import net.minecraft.world.TestableWorld
import net.minecraft.world.gen.feature.TreeFeatureConfig
import net.minecraft.world.gen.foliage.FoliagePlacer

class ApricornFoliagePlacer(
    val radius: IntProvider,
    val offset: IntProvider
) : FoliagePlacer(radius, offset) {
    override fun getType() = CobblemonFoliagePlacers.APRICORN_FOLIAGE_PLACER_TYPE

    override fun generate(
        world: TestableWorld?,
        placer: BlockPlacer?,
        random: Random?,
        config: TreeFeatureConfig?,
        trunkHeight: Int,
        treeNode: TreeNode?,
        foliageHeight: Int,
        radius: Int,
        offset: Int
    ) {
        println("Generating Apricorn Foliage")
    }

    override fun getRandomHeight(
        random: Random?,
        trunkHeight: Int,
        config: TreeFeatureConfig?
    ): Int {
        return 0
    }

    override fun isInvalidForLeaves(
        random: Random?,
        dx: Int,
        y: Int,
        dz: Int,
        radius: Int,
        giantTrunk: Boolean
    ): Boolean {
        return false
    }

    companion object {
        val CODEC = RecordCodecBuilder.mapCodec {
            it.group(
                IntProvider.createValidatingCodec(0, 16).fieldOf("radius").forGetter<ApricornFoliagePlacer> {it.radius},
                IntProvider.createValidatingCodec(0, 16).fieldOf("offset").forGetter<ApricornFoliagePlacer> {it.offset}
            ).apply(it, ::ApricornFoliagePlacer)
        }
        val ID = cobblemonResource("apricorn_foliage_placer")
    }

}