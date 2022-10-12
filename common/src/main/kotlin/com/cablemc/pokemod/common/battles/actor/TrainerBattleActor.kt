/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.battles.actor

import com.cablemc.pokemod.common.api.battles.model.actor.AIBattleActor
import com.cablemc.pokemod.common.api.battles.model.actor.ActorType
import com.cablemc.pokemod.common.api.battles.model.ai.BattleAI
import com.cablemc.pokemod.common.battles.pokemon.BattlePokemon
import com.cablemc.pokemod.common.util.asTranslated
import java.util.UUID

class TrainerBattleActor(
    val trainerName: String,
    uuid: UUID,
    pokemonList: List<BattlePokemon>,
    artificialDecider: BattleAI
) : AIBattleActor(uuid, pokemonList, artificialDecider) {
    override fun getName() = trainerName.asTranslated()
    override val type = ActorType.NPC
}