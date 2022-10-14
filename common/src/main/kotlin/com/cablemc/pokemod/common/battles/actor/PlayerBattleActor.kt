/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.battles.actor

import com.cablemc.pokemod.common.Pokemod
import com.cablemc.pokemod.common.PokemodNetwork
import com.cablemc.pokemod.common.api.battles.model.actor.ActorType
import com.cablemc.pokemod.common.api.battles.model.actor.BattleActor
import com.cablemc.pokemod.common.api.battles.model.actor.EntityBackedBattleActor
import com.cablemc.pokemod.common.api.net.NetworkPacket
import com.cablemc.pokemod.common.api.pokemon.experience.BattleExperienceSource
import com.cablemc.pokemod.common.api.text.red
import com.cablemc.pokemod.common.battles.pokemon.BattlePokemon
import com.cablemc.pokemod.common.util.getPlayer
import java.util.UUID
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.MutableText

class PlayerBattleActor(
    uuid: UUID,
    pokemonList: List<BattlePokemon>
) : BattleActor(uuid, pokemonList.toMutableList()), EntityBackedBattleActor<ServerPlayerEntity> {

    override val entity: ServerPlayerEntity?
        get() = this.uuid.getPlayer()

    override fun getName(): MutableText = this.entity?.name?.copy() ?: "Offline Player".red()
    override val type = ActorType.PLAYER
    override fun getPlayerUUIDs() = setOf(uuid)
    override fun awardExperience(battlePokemon: BattlePokemon, experience: Int) {
        if (battle.isPvP && !Pokemod.config.allowExperienceFromPvP) {
            return
        }

        val source = BattleExperienceSource(battle, battlePokemon.facedOpponents.toList())
        if (battlePokemon.effectedPokemon == battlePokemon.originalPokemon && experience > 0) {
            uuid.getPlayer()
                ?.let { battlePokemon.effectedPokemon.addExperienceWithPlayer(it, source, experience) }
                ?: run { battlePokemon.effectedPokemon.addExperience(source, experience) }
        }
    }

    override fun sendUpdate(packet: NetworkPacket) {
        PokemodNetwork.sendToPlayers(getPlayerUUIDs().mapNotNull { it.getPlayer() }, packet)
    }
}
