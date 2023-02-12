/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common

import com.cobblemon.mod.common.api.pokeball.PokeBalls
import com.cobblemon.mod.common.entity.pokeball.EmptyPokeBallEntity
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.registry.PlatformRegistry
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.SpawnGroup
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys

object CobblemonEntities : PlatformRegistry<Registry<EntityType<*>>, RegistryKey<Registry<EntityType<*>>>, EntityType<*>>() {

    override val registry: Registry<EntityType<*>> = Registries.ENTITY_TYPE
    override val registryKey: RegistryKey<Registry<EntityType<*>>> = RegistryKeys.ENTITY_TYPE

    val POKEMON_KEY = cobblemonResource("pokemon")
    val POKEMON: EntityType<PokemonEntity> = this.create(
        POKEMON_KEY.path,
        EntityType.Builder.create({ _, world -> PokemonEntity(world) }, SpawnGroup.CREATURE)
            .build(POKEMON_KEY.toString())
    )

    val EMPTY_POKEBALL_KEY = cobblemonResource("empty_pokeball")
    val EMPTY_POKEBALL: EntityType<EmptyPokeBallEntity> = this.create(
        EMPTY_POKEBALL_KEY.path,
        EntityType.Builder.create({ _, world -> EmptyPokeBallEntity(PokeBalls.POKE_BALL, world) }, SpawnGroup.MISC)
            .build(EMPTY_POKEBALL_KEY.toString())
    )

    fun registerAttributes(consumer: (EntityType<out LivingEntity>, DefaultAttributeContainer.Builder) -> Unit) {
        consumer(POKEMON, PokemonEntity.createAttributes())
    }

}