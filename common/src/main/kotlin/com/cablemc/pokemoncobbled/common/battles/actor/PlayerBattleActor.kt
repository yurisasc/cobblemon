package com.cablemc.pokemoncobbled.common.battles.actor

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.battles.pokemon.BattlePokemon
import com.cablemc.pokemoncobbled.common.api.battles.model.actor.ActorType
import com.cablemc.pokemoncobbled.common.api.battles.model.actor.EntityBattleActor
import com.cablemc.pokemoncobbled.common.util.getPlayer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.MutableText

class PlayerBattleActor(
    entity: ServerPlayerEntity,
    pokemonList: List<BattlePokemon>
) : EntityBattleActor<ServerPlayerEntity>(entity, pokemonList.toMutableList()) {

//    override fun sendMessage(component: Text) = getPlayerEntity()?.sendServerMessage(component) ?: Unit
    override val type = ActorType.PLAYER
    override fun getPlayerUUIDs() = setOf(this.entity.uuid)
    override fun awardExperience(battlePokemon: BattlePokemon, experience: Int) {
        if (battlePokemon.effectedPokemon == battlePokemon.originalPokemon && experience > 0) {
            battlePokemon.effectedPokemon.addExperienceWithPlayer(this.entity, experience)
        }
    }

    override fun sendUpdate(packet: NetworkPacket) {
        CobbledNetwork.sendToPlayer(this.entity, packet)
    }

}