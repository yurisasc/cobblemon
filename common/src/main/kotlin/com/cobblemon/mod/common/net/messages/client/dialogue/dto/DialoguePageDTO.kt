/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.client.dialogue.dto

import com.cobblemon.mod.common.api.dialogue.ActiveDialogue
import com.cobblemon.mod.common.api.dialogue.DialoguePage
import com.cobblemon.mod.common.api.net.Decodable
import com.cobblemon.mod.common.api.net.Encodable
import com.cobblemon.mod.common.api.text.text
import net.minecraft.network.PacketByteBuf
import net.minecraft.text.MutableText

class DialoguePageDTO : Encodable, Decodable {
    var speaker: String? = null
    var lines: MutableList<MutableText> = mutableListOf()
    // Later can include some face data probably
    var clientActions = mutableListOf<String>()

    constructor()
    constructor(dialoguePage: DialoguePage, activeDialogue: ActiveDialogue) {
        this.speaker = dialoguePage.speaker
        this.lines = dialoguePage.lines.map { it(activeDialogue) }.toMutableList()
        this.clientActions = dialoguePage.clientActions.map { it.originalString }.toMutableList()
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeNullable(speaker) { _, value -> buffer.writeString(value)}
        buffer.writeCollection(lines) { _, value -> buffer.writeText(value) }
        buffer.writeInt(clientActions.size)
        clientActions.forEach { buffer.writeString(it) }
    }

    override fun decode(buffer: PacketByteBuf) {
        speaker = buffer.readNullable { buffer.readString() }
        lines = buffer.readList { it.readText().copy() }.toMutableList()
        val clientActionsSize = buffer.readInt()
        for (i in 0 until clientActionsSize) {
            clientActions.add(buffer.readString())
        }
    }
}