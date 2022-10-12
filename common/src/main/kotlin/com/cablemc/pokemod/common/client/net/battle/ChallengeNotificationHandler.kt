/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.net.battle

import com.cablemc.pokemod.common.PokemodNetwork
import com.cablemc.pokemod.common.api.text.lightPurple
import com.cablemc.pokemod.common.client.keybind.currentKey
import com.cablemc.pokemod.common.client.keybind.keybinds.PartySendBinding
import com.cablemc.pokemod.common.client.net.ClientPacketHandler
import com.cablemc.pokemod.common.net.messages.client.battle.ChallengeNotificationPacket
import com.cablemc.pokemod.common.util.lang
import net.minecraft.client.MinecraftClient

object ChallengeNotificationHandler : ClientPacketHandler<ChallengeNotificationPacket> {
    override fun invokeOnClient(packet: ChallengeNotificationPacket, ctx: PokemodNetwork.NetworkContext) {
        MinecraftClient.getInstance().player?.sendMessage(
            lang(
                "challenge.receiver",
                packet.challengerName,
                PartySendBinding.currentKey().localizedText
            ).lightPurple()
        )
    }
}