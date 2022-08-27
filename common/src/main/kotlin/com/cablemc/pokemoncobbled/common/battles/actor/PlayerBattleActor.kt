package com.cablemc.pokemoncobbled.common.battles.actor

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.battles.model.actor.ActorType
import com.cablemc.pokemoncobbled.common.api.battles.model.actor.BattleActor
import com.cablemc.pokemoncobbled.common.api.battles.model.actor.EntityBackedBattleActor
import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.api.pokemon.experience.BattleExperienceSource
import com.cablemc.pokemoncobbled.common.api.text.red
import com.cablemc.pokemoncobbled.common.battles.pokemon.BattlePokemon
import com.cablemc.pokemoncobbled.common.util.getPlayer
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
        if (battle.isPvP && !PokemonCobbled.config.allowExperienceFromPvP) {
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
        CobbledNetwork.sendToPlayers(getPlayerUUIDs().mapNotNull { it.getPlayer() }, packet)
    }
}
