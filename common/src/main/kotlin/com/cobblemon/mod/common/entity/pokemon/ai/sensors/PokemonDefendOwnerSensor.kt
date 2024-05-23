package com.cobblemon.mod.common.entity.pokemon.ai.sensors

import com.cobblemon.mod.common.CobblemonMemories
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.brain.LivingTargetCache
import net.minecraft.entity.ai.brain.MemoryModuleType
import net.minecraft.entity.ai.brain.sensor.Sensor
import net.minecraft.server.world.ServerWorld

class DefendOwnerSensor : Sensor<PokemonEntity>(100) {

    override fun getOutputMemoryModules() = setOf(CobblemonMemories.NEAREST_VISIBLE_ATTACKER)

    override fun sense(world: ServerWorld, entity: PokemonEntity) {
        val owner = entity.owner ?: return
        entity.brain.getOptionalRegisteredMemory(MemoryModuleType.VISIBLE_MOBS).ifPresent { visibleMobs ->
            setNearestAttacker(entity, visibleMobs, owner)
        }
    }

    private fun setNearestAttacker(entity: PokemonEntity, visibleMobs: LivingTargetCache, owner: LivingEntity) {
        val nearestAttacker = visibleMobs.findFirst { mob ->
            mob.attacking == owner
        }.map { mob -> mob as LivingEntity }

        entity.brain.remember(CobblemonMemories.NEAREST_VISIBLE_ATTACKER, nearestAttacker)
    }
}
