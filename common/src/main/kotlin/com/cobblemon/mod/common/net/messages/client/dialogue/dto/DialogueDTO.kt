/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.client.dialogue.dto

import com.cobblemon.mod.common.api.dialogue.ActiveDialogue
import com.cobblemon.mod.common.api.dialogue.input.DialogueAutoContinueInput
import com.cobblemon.mod.common.api.dialogue.input.DialogueOptionSetInput
import com.cobblemon.mod.common.api.dialogue.input.DialogueTextInput
import com.cobblemon.mod.common.api.net.Decodable
import com.cobblemon.mod.common.api.net.Encodable
import java.util.UUID
import net.minecraft.network.PacketByteBuf

class DialogueDTO : Encodable, Decodable {
    lateinit var dialogueId: UUID
    lateinit var currentPageDTO: DialoguePageDTO
    lateinit var dialogueInput: DialogueInputDTO

    constructor()
    constructor(activeDialogue: ActiveDialogue) {
        this.dialogueId = activeDialogue.dialogueId
        this.currentPageDTO = DialoguePageDTO(activeDialogue.currentPage, activeDialogue)
        val input = activeDialogue.activeInput.dialogueInput
        this.dialogueInput = when (input) {
            is DialogueOptionSetInput -> DialogueInputDTO(input, activeDialogue)
            is DialogueAutoContinueInput -> DialogueInputDTO(input, activeDialogue)
            is DialogueTextInput -> DialogueInputDTO(input, activeDialogue)
            else -> DialogueInputDTO(activeDialogue) // Uses InputType.NONE
        }
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeUuid(dialogueId)
        currentPageDTO.encode(buffer)
        dialogueInput.encode(buffer)
    }

    override fun decode(buffer: PacketByteBuf) {
        dialogueId = buffer.readUuid()
        currentPageDTO = DialoguePageDTO()
        currentPageDTO.decode(buffer)
        dialogueInput = DialogueInputDTO()
        dialogueInput.decode(buffer)
    }
}