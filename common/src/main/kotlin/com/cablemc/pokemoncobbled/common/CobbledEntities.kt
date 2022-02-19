package com.cablemc.pokemoncobbled.common

import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.cablemc.pokemoncobbled.common.entity.pokeball.EmptyPokeBallEntity
import com.cablemc.pokemoncobbled.common.api.pokeball.PokeBalls
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import dev.architectury.registry.registries.DeferredRegister
import net.minecraft.core.Registry
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory

object CobbledEntities {
    private val registry = DeferredRegister.create(PokemonCobbled.MODID, Registry.ENTITY_TYPE_REGISTRY)
    fun register() = registry.register()
    private fun <T : Entity> entity(
        name: String,
        builder: EntityType.Builder<T>
    ) = registry.register(name) {
        builder
            .build(cobbledResource(name).toString())
    }

    val POKEMON = entity(
        name = "pokemon",
        builder = EntityType.Builder.of<PokemonEntity>(
            { _, level -> PokemonEntity(level) },
            MobCategory.CREATURE
        )
    )

    val EMPTY_POKEBALL = entity(
        name = "empty_pokeball",
        builder = EntityType.Builder.of<EmptyPokeBallEntity>(
            { _, level -> EmptyPokeBallEntity(PokeBalls.POKE_BALL, level) },
            MobCategory.MISC
        )
    )

    val POKEMON_TYPE: EntityType<PokemonEntity>
        get() = POKEMON.get()

    val EMPTY_POKEBALL_TYPE: EntityType<EmptyPokeBallEntity>
        get() = EMPTY_POKEBALL.get()
}