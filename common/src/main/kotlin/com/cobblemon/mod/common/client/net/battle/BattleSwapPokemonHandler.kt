/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.net.battle

import com.cobblemon.mod.common.api.net.ClientNetworkPacketHandler
import com.cobblemon.mod.common.client.CobblemonClient
import com.cobblemon.mod.common.client.battle.ActiveClientBattlePokemon
import com.cobblemon.mod.common.client.battle.ClientBattlePokemon
import com.cobblemon.mod.common.client.battle.animations.MoveTileOffscreenAnimation
import com.cobblemon.mod.common.client.battle.animations.SwapAndMoveTileOnscreenAnimation
import com.cobblemon.mod.common.net.messages.client.battle.BattleSwapPokemonPacket
import net.minecraft.client.MinecraftClient

object BattleSwapPokemonHandler : ClientNetworkPacketHandler<BattleSwapPokemonPacket> {
    override fun handle(packet: BattleSwapPokemonPacket, client: MinecraftClient) {
        val battle = CobblemonClient.battle ?: return
        val (actor, activeBattlePokemon) = battle.getPokemonFromPNX(packet.pnx)

        val swapPokemon = activeBattlePokemon.getAdjacentAllies().first() as ActiveClientBattlePokemon
        val lastAnimation = activeBattlePokemon.animations.lastOrNull()
        if (lastAnimation !is MoveTileOffscreenAnimation) {
            activeBattlePokemon.animations.add(MoveTileOffscreenAnimation())
        }



        activeBattlePokemon.animations.add(
            SwapAndMoveTileOnscreenAnimation(
                    swapPokemon.battlePokemon as ClientBattlePokemon
            )
        )

        swapPokemon.animations.add(
            SwapAndMoveTileOnscreenAnimation(
                    activeBattlePokemon.battlePokemon as ClientBattlePokemon
            )
        )

        // Only update currently selected Pokémon if it's our Pokémon being switched in
//        if (actor == battle.getParticipatingActor(client.session.profile.id)) {
//            CobblemonClient.storage.switchToPokemon(packet.newPokemon.uuid)
//        }
    }
}