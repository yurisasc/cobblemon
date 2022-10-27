/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.battles.actor

import com.cablemc.pokemod.common.api.battles.model.actor.AIBattleActor
import com.cablemc.pokemod.common.api.battles.model.actor.ActorType
import com.cablemc.pokemod.common.api.battles.model.ai.BattleAI
import com.cablemc.pokemod.common.api.text.text
import com.cablemc.pokemod.common.battles.ai.RandomBattleAI
import com.cablemc.pokemod.common.battles.pokemon.BattlePokemon
import java.util.UUID
import net.minecraft.text.MutableText
class MultiPokemonBattleActor(
    pokemonList: List<BattlePokemon>,
    artificialDecider: BattleAI = RandomBattleAI(),
    uuid: UUID = UUID.randomUUID()
) : AIBattleActor(uuid, pokemonList, artificialDecider) {
    override fun getName(): MutableText = "Wild Pokémon".text() // TODO probably remove by making it nullable
    override val type = ActorType.WILD
}