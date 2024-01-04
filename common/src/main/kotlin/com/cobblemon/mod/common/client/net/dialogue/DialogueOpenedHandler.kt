/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.net.dialogue

import com.cobblemon.mod.common.api.net.ClientNetworkPacketHandler
import com.cobblemon.mod.common.client.gui.dialogue.DialogueScreen
import com.cobblemon.mod.common.net.messages.client.dialogue.DialogueOpenedPacket
import net.minecraft.client.MinecraftClient

object DialogueOpenedHandler: ClientNetworkPacketHandler<DialogueOpenedPacket> {
    override fun handle(packet: DialogueOpenedPacket, client: MinecraftClient) {
        val currentScreen = client.currentScreen as? DialogueScreen
        if (currentScreen != null && currentScreen.dialogueId == packet.dialogueDTO.dialogueId) {
            currentScreen.update(packet.dialogueDTO)
        } else {
            client.setScreen(DialogueScreen(packet.dialogueDTO))
        }
    }
}