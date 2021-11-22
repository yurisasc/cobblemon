package com.cablemc.pokemoncobbled.common.entity.pokemon

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.AgeableMob
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.level.Level

class PokemonEntity(
    type: EntityType<out PokemonEntity>,
    level: Level
) : TamableAnimal(type, level) {
    override fun getBreedOffspring(level: ServerLevel, partner: AgeableMob): AgeableMob? {
        return null
    }
}