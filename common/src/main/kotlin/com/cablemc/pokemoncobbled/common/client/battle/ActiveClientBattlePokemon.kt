package com.cablemc.pokemoncobbled.common.client.battle

import com.cablemc.pokemoncobbled.common.api.gui.ColourLibrary.SIDE_1_ALLY_BATTLE_COLOUR
import com.cablemc.pokemoncobbled.common.api.gui.ColourLibrary.SIDE_1_BATTLE_COLOUR
import com.cablemc.pokemoncobbled.common.api.gui.ColourLibrary.SIDE_2_ALLY_BATTLE_COLOUR
import com.cablemc.pokemoncobbled.common.api.gui.ColourLibrary.SIDE_2_BATTLE_COLOUR
import com.cablemc.pokemoncobbled.common.battles.Targetable
import com.cablemc.pokemoncobbled.common.client.battle.animations.TileAnimation
import net.minecraft.client.MinecraftClient
import java.util.concurrent.ConcurrentLinkedQueue

class ActiveClientBattlePokemon(val actor: ClientBattleActor, var battlePokemon: ClientBattlePokemon?) : Targetable {
    var animations = ConcurrentLinkedQueue<TileAnimation>()
    var xDisplacement = 0F
    var invisibleX = -1F
    var ballCapturing: ClientBallDisplay? = null

    override fun getAllActivePokemon() =  actor.side.battle.sides.flatMap { it.activeClientBattlePokemon }
    override fun getActorPokemon() = actor.activePokemon
    override fun getSidePokemon() = actor.side.activeClientBattlePokemon
    override fun getActorShowdownId() = actor.showdownId
    override fun getFormat() = actor.side.battle.battleFormat
    override fun isAllied(other: Targetable) = actor.side == (other as ActiveClientBattlePokemon).actor.side
    override fun hasPokemon() = battlePokemon != null
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

    fun animate(deltaTicks: Float) {
        val animation = animations.peek() ?: return
        if (animation.invoke(this, deltaTicks)) {
            if (!animation.shouldHoldUntilNextAnimation() || animations.size > 1) {
                animations.remove()
            }
        }
    }
}