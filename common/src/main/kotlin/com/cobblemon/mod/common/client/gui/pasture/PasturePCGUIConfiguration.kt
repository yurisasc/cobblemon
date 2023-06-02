/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.pasture

import com.cobblemon.mod.common.CobblemonNetwork
import com.cobblemon.mod.common.api.reactive.SettableObservable
import com.cobblemon.mod.common.client.gui.pc.PCGUIConfiguration
import com.cobblemon.mod.common.net.messages.client.pasture.OpenPasturePacket
import com.cobblemon.mod.common.net.messages.server.pasture.PasturePokemonPacket
import java.util.UUID
import net.minecraft.util.math.BlockPos

class PasturePCGUIConfiguration(
    val pastureId: UUID,
    val pasturePos: BlockPos,
    val pasturedPokemon: SettableObservable<List<OpenPasturePacket.PasturePokemonDataDTO>>
) : PCGUIConfiguration(
    exitFunction = { it.closeNormally(unlink = true) },
    showParty = false,
    selectOverride = { pcGui, position, pokemon ->
        if (pokemon != null && pokemon.tetheringId == null) {
            pcGui.closeNormally(unlink = false)
            CobblemonNetwork.sendToServer(PasturePokemonPacket(pokemonId = pokemon.uuid, pasturePos = pasturePos))
        }
    }
) {

}
