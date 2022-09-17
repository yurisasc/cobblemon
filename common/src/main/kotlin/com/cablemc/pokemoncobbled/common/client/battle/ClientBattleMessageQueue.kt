/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.client.battle

import net.minecraft.text.OrderedText

/**
 * A wrapper around the client battle message information. This is essentially an appending list of all
 * the battle messages the client knows about. It keeps track of where it is up to, so they must actively
 * progress the queue to see newer messages.
 *
 * @author Hiroku
 * @since June 24th, 2022
 */
class ClientBattleMessageQueue {
    var listener: (OrderedText) -> Unit = {}
    private val messages = mutableListOf<OrderedText>()

    fun add(messages: Iterable<OrderedText>) {
        this.messages.addAll(messages)
        messages.forEach(listener)
    }

    fun subscribe(listener: (OrderedText) -> Unit) {
        this.listener = listener
        messages.forEach(listener)
    }
}