package com.cablemc.pokemoncobbled.common.battles.actor

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.api.battles.model.actor.BattleActor
import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.api.text.text
import com.cablemc.pokemoncobbled.common.battles.pokemon.BattlePokemon
import com.cablemc.pokemoncobbled.common.api.battles.model.actor.ActorType
import com.cablemc.pokemoncobbled.common.api.battles.model.actor.EntityBackedBattleActor
import com.cablemc.pokemoncobbled.common.util.getPlayer
import net.minecraft.server.network.ServerPlayerEntity
import java.util.UUID
import net.minecraft.text.MutableText

class PlayerBattleActor(
    uuid: UUID,
    pokemonList: List<BattlePokemon>
) : BattleActor(uuid, pokemonList.toMutableList()), EntityBackedBattleActor<ServerPlayerEntity> {

    // ToDo pending exception handling on battles for the how to handle if for some reason null.
    override val entity: ServerPlayerEntity
        get() = this.uuid.getPlayer()!!

    //    override fun sendMessage(component: Text) = getPlayerEntity()?.sendServerMessage(component) ?: Unit
    override fun getName(): MutableText = this.entity.name.copy()
    override val type = ActorType.PLAYER
    override fun getPlayerUUIDs() = setOf(uuid)
    override fun awardExperience(battlePokemon: BattlePokemon, experience: Int) {
        if (battlePokemon.effectedPokemon == battlePokemon.originalPokemon && experience > 0) {
            uuid.getPlayer()
                ?.let { battlePokemon.effectedPokemon.addExperienceWithPlayer(it, experience) }
                ?: run { battlePokemon.effectedPokemon.addExperience(experience) }
        }
    }

    override fun sendUpdate(packet: NetworkPacket) {
        CobbledNetwork.sendToPlayers(getPlayerUUIDs().mapNotNull { it.getPlayer() }, packet)
    }
}
