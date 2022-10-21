/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common

import com.cablemc.pokemod.common.api.events.PokemodEvents
import com.cablemc.pokemod.common.api.events.entity.EntityAttributeEvent
import com.cablemc.pokemod.common.api.pokeball.PokeBalls
import com.cablemc.pokemod.common.entity.pokeball.EmptyPokeBallEntity
import com.cablemc.pokemod.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemod.common.registry.CompletableRegistry
import com.cablemc.pokemod.common.util.pokemodResource
import dev.architectury.registry.level.entity.EntityAttributeRegistry
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.SpawnGroup
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.util.registry.Registry

object PokemodEntities : CompletableRegistry<EntityType<*>>(Registry.ENTITY_TYPE_KEY) {
    override fun register() {
        super.register()

        EntityAttributeRegistry.register(
            { com.cablemc.pokemod.common.PokemodEntities.POKEMON_TYPE },
            {
                DefaultAttributeContainer
                    .builder()
                    .add(EntityAttributes.GENERIC_FOLLOW_RANGE)
                    .add(EntityAttributes.GENERIC_MAX_HEALTH)
                    .add(EntityAttributes.GENERIC_MOVEMENT_SPEED)
                    .add(EntityAttributes.HORSE_JUMP_STRENGTH)
                    .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)
                    .add(EntityAttributes.GENERIC_ARMOR)
                    .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS)
                    .also { PokemodEvents.ENTITY_ATTRIBUTE.post(EntityAttributeEvent(PokemodEntities.POKEMON_TYPE, it)) }
            }
        )
    }
    private fun <T : Entity> entity(
        name: String,
        entityTypeBuilder: EntityType.Builder<T>
    ): RegistrySupplier<EntityType<T>> {
        return queue(name) { entityTypeBuilder.build(pokemodResource(name).toString()) }
    }

    private fun <T : LivingEntity> livingEntity(
        name: String,
        entityTypeBuilder: EntityType.Builder<T>
    ): RegistrySupplier<EntityType<T>> {
        return queue(name) { entityTypeBuilder.build(pokemodResource("pokemon").toString()) }
    }

    val POKEMON = com.cablemc.pokemod.common.PokemodEntities.livingEntity(
        name = "pokemon",
        entityTypeBuilder = EntityType.Builder.create<PokemonEntity>(
            { _, world -> PokemonEntity(world) },
            SpawnGroup.CREATURE
        )
    )

    val EMPTY_POKEBALL = com.cablemc.pokemod.common.PokemodEntities.entity(
        name = "empty_pokeball",
        entityTypeBuilder = EntityType.Builder.create<EmptyPokeBallEntity>(
            { _, world -> EmptyPokeBallEntity(PokeBalls.POKE_BALL, world) },
            SpawnGroup.MISC
        )
    )

    // TODO remove these because they're pretty hair brained
    val POKEMON_TYPE: EntityType<PokemonEntity>
        get() = com.cablemc.pokemod.common.PokemodEntities.POKEMON.get()

    val EMPTY_POKEBALL_TYPE: EntityType<EmptyPokeBallEntity>
        get() = com.cablemc.pokemod.common.PokemodEntities.EMPTY_POKEBALL.get()
}