package com.cablemc.pokemoncobbled.common.battles.actor

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.api.battles.model.actor.BattleActor
import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.api.text.text
import com.cablemc.pokemoncobbled.common.battles.pokemon.BattlePokemon
import com.cablemc.pokemoncobbled.common.util.getPlayer
import com.cablemc.pokemoncobbled.common.util.sendServerMessage
import java.util.UUID
import net.minecraft.text.MutableText
import net.minecraft.text.Text

class PlayerBattleActor(
    uuid: UUID,
    pokemonList: List<BattlePokemon>
) : BattleActor(uuid, pokemonList.toMutableList()) {
    fun getPlayerEntity() = uuid.getPlayer()
    override fun sendMessage(component: Text) = getPlayerEntity()?.sendServerMessage(component) ?: Unit
    override fun getName(): MutableText = getPlayerEntity()?.name?.copy() ?: "".text()

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