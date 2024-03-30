package com.cobblemon.mod.common.client.net.battle

import com.cobblemon.mod.common.api.net.ClientNetworkPacketHandler
import com.cobblemon.mod.common.client.CobblemonClient
import com.cobblemon.mod.common.client.battle.ClientBattlePokemon
import com.cobblemon.mod.common.net.messages.client.battle.BattleReplacePokemonPacket
import net.minecraft.client.MinecraftClient

/**
 * The handler for [BattleReplacePokemonPacket]s. Removes the illusion [ClientBattlePokemon] and replaces it with the actual data.
 *
 * @author Segfault Guy
 * @since March 30th, 2024
 */
object BattleReplacePokemonHandler : ClientNetworkPacketHandler<BattleReplacePokemonPacket> {
    override fun handle(packet: BattleReplacePokemonPacket, client: MinecraftClient) {
        val battle = CobblemonClient.battle ?: return
        val (actor, activeBattlePokemon) = battle.getPokemonFromPNX(packet.pnx)

        with(packet.realPokemon) {
            activeBattlePokemon.battlePokemon = ClientBattlePokemon(
                uuid = uuid,
                displayName = displayName,
                properties = properties,
                aspects = aspects,
                hpValue = hpValue,
                maxHp = maxHp,
                isHpFlat = packet.isAlly,
                status = status,
                statChanges = statChanges
            ).also {
                it.actor = actor
            }
        }
    }
}