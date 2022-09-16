/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.battles.runner

interface ShowdownConnection {
    fun open()
    fun close()
    fun write(input: String)
    fun read(messageHandler: (String) -> Unit)
    fun isClosed(): Boolean
    fun isConnected(): Boolean

    companion object {
        const val LINE_END = "{EOT}"
        const val LINE_START = "{SOT}"
    }
}