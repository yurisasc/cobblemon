package com.cablemc.pokemoncobbled.common.entity.pokeball

import com.cablemc.pokemoncobbled.common.api.pokemon.pokeball.PokeBalls
import com.cablemc.pokemoncobbled.common.entity.EntityRegistry
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.pokemon.pokeball.PokeBall
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

class OccupiedPokeBallEntity(
    pokeBall: PokeBall,
    val pokemon: Pokemon,
    entityType: EntityType<out OccupiedPokeBallEntity>,
    level: Level
) : PokeBallEntity(pokeBall, entityType, level) {

    constructor(
        entityType: EntityType<out OccupiedPokeBallEntity>,
        pokemon: Pokemon,
        level: Level
    ) : this(PokeBalls.POKE_BALL, pokemon, entityType, level)

    constructor(pokeBall: PokeBall, pokemon: Pokemon, level: Level) : this(
        pokeBall,
        pokemon,
        EntityRegistry.OCCUPIED_POKEBALL.get(),
        level
    )

}