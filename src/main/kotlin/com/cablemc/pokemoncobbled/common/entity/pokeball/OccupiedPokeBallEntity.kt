package com.cablemc.pokemoncobbled.common.entity.pokeball

import com.cablemc.pokemoncobbled.common.api.pokeball.PokeBalls
import com.cablemc.pokemoncobbled.common.entity.EntityRegistry
import com.cablemc.pokemoncobbled.common.pokeball.PokeBall
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

class OccupiedPokeBallEntity(
    pokeBall: PokeBall,
    val pokemon: Pokemon,
    entityType: EntityType<out OccupiedPokeBallEntity>,
    level: Level
) : PokeBallEntity(pokeBall, entityType, level) {
    override val delegate = if (level.isClientSide) {
        com.cablemc.pokemoncobbled.client.entity.OccupiedPokeBallClientDelegate()
    } else {
        OccupiedPokeBallServerDelegate()
    }

    init {
        delegate.initialize(this)
    }

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