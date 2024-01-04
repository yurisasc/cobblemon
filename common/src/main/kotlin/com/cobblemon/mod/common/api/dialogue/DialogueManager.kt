/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.dialogue

import com.cobblemon.mod.common.CobblemonNetwork.sendPacket
import com.cobblemon.mod.common.net.messages.client.dialogue.DialogueClosedPacket
import com.cobblemon.mod.common.net.messages.client.dialogue.DialogueOpenedPacket
import com.cobblemon.mod.common.util.activeDialogue
import java.util.UUID
import net.minecraft.server.network.ServerPlayerEntity

/**
 * Manages the active dialogues for players. Map is indexed by player UUID.
 * You really need to make sure any dialogues you start with a player go through this otherwise
 * the player won't be able to close the dialogue.
 *
 * @author Hiroku
 * @since December 27th, 2023
 */
object DialogueManager {
    val activeDialogues = mutableMapOf<UUID, ActiveDialogue>()

    fun startDialogue(playerEntity: ServerPlayerEntity, dialogue: Dialogue) {
        val activeDialogue = ActiveDialogue(playerEntity, dialogue)
        activeDialogues[playerEntity.uuid] = activeDialogue
        val packet = DialogueOpenedPacket(activeDialogue, includeFaces = true)
        playerEntity.sendPacket(packet)
    }

    fun stopDialogue(playerEntity: ServerPlayerEntity) {
        val activeDialogue = playerEntity.activeDialogue ?: return
        DialogueClosedPacket(activeDialogue.dialogueId).sendToPlayer(playerEntity)
        activeDialogues.remove(activeDialogue.dialogueId)
    }
}