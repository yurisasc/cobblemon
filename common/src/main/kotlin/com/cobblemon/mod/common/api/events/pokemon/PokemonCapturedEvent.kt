/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.events.pokemon

import com.bedrockk.molang.runtime.struct.QueryStruct
import com.cobblemon.mod.common.api.molang.MoLangFunctions.addStandardFunctions
import com.cobblemon.mod.common.api.molang.MoLangFunctions.asMoLangValue
import com.cobblemon.mod.common.entity.pokeball.EmptyPokeBallEntity
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.itemRegistry
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.network.ServerPlayerEntity

data class PokemonCapturedEvent (
    val pokemon: Pokemon,
    val player: ServerPlayerEntity,
    val pokeBallEntity: EmptyPokeBallEntity
) {
    val struct = QueryStruct(hashMapOf())
        .addFunction("pokemon") { pokemon.struct }
        .addFunction("player") { player.asMoLangValue() }
        .addFunction("poke_ball") { pokeBallEntity.struct }
        .addFunction("item") { player.world.itemRegistry.getEntry(pokeBallEntity.pokeBall.item).asMoLangValue(RegistryKeys.ITEM) }
}