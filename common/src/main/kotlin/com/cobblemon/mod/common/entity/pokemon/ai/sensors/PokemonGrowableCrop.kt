package com.cobblemon.mod.common.entity.pokemon.ai.sensors

import com.cobblemon.mod.common.CobblemonMemories
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.block.CropBlock
import net.minecraft.entity.ai.brain.MemoryModuleType
import net.minecraft.entity.ai.brain.sensor.Sensor
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import java.util.*

class PokemonGrowableCropSensor : Sensor<PokemonEntity>() {
    override fun sense(world: ServerWorld, entity: PokemonEntity) {
        findBoneMealPos(world, entity).ifPresent {
            entity.brain.remember(CobblemonMemories.NEARBY_GROWABLE_CROPS, it)
        }
    }

    override fun getOutputMemoryModules(): Set<MemoryModuleType<*>> {
        return setOf(CobblemonMemories.NEARBY_GROWABLE_CROPS)
    }

    private fun findBoneMealPos(world: ServerWorld, entity: PokemonEntity): Optional<BlockPos> {
        val mutable = BlockPos.Mutable()
        var optional = Optional.empty<BlockPos>()
        var i = 0

        //todo(broccoli): cleanup
        for (j in -1..1) {
            for (k in -1..1) {
                for (l in -1..1) {
                    mutable[entity.blockPos, j, k] = l
                    if (canBoneMeal(mutable, world)) {
                        ++i
                        if (world.random.nextInt(i) == 0) {
                            optional = Optional.of(mutable.toImmutable())
                        }
                    }
                }
            }
        }

        return optional
    }

    private fun canBoneMeal(pos: BlockPos, world: ServerWorld): Boolean {
        val blockState = world.getBlockState(pos)
        val block = blockState.block

        return block is CropBlock && !block.isMature(blockState)
    }
}