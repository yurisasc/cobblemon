package com.cablemc.pokemoncobbled.common.client.battle

import com.cablemc.pokemoncobbled.common.api.gui.ColourLibrary.SIDE_1_ALLY_BATTLE_COLOUR
import com.cablemc.pokemoncobbled.common.api.gui.ColourLibrary.SIDE_1_BATTLE_COLOUR
import com.cablemc.pokemoncobbled.common.api.gui.ColourLibrary.SIDE_2_ALLY_BATTLE_COLOUR
import com.cablemc.pokemoncobbled.common.api.gui.ColourLibrary.SIDE_2_BATTLE_COLOUR
import net.minecraft.client.MinecraftClient

class ActiveClientBattlePokemon(var battlePokemon: ClientBattlePokemon?) {
    fun getHue(): Int {
        val playerUUID = MinecraftClient.getInstance().player?.uuid ?: return 0xFAFAFA
        val actor = battlePokemon?.actor ?: return 0xFAFAFA
        val side = actor.side
        val battle = actor.side.battle
        val playerActor = battle.sides.flatMap { it.actors }.find { it.uuid == playerUUID }
        return if (playerActor != null) {
            if (playerActor.side != side) {
                // Enemies
                if (side.actors.indexOf(actor) == 0) {
                    SIDE_2_BATTLE_COLOUR
                } else {
                    SIDE_2_ALLY_BATTLE_COLOUR
                }
            } else {
                // Allies
                if (actor == playerActor) {
                    SIDE_1_BATTLE_COLOUR
                } else {
                    SIDE_1_ALLY_BATTLE_COLOUR
                }
            }
        } else {
            if (side == battle.side1) {
                SIDE_1_BATTLE_COLOUR
            } else {
                SIDE_2_BATTLE_COLOUR
            }
        }
    }

}