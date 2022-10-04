/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.net.messages.client.data

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

abstract class DataRegistrySyncPacket<T>(private val registryEntries: Collection<T>) : NetworkPacket {

    constructor() : this(emptyList())

    internal val entries = hashMapOf<Identifier, T>()

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeInt(this.entries.size)
        this.entries.forEach { (identifier, entry) ->
            buffer.writeIdentifier(identifier)
            this.encodeEntry(buffer, entry)
        }
    }

    override fun decode(buffer: PacketByteBuf) {
        this.entries.clear()
        repeat(buffer.readInt()) {
            val identifier = buffer.readIdentifier()
            val entry = this.decodeEntry(buffer)
            if (entry != null) {
                this.entries[identifier] = entry
            }
        }
    }

    /**
     * Encodes an entry of type [T] to the [PacketByteBuf].
     *
     * @param buffer The [PacketByteBuf] being encoded to.
     * @param entry The entry of type [T].
     */
    abstract fun encodeEntry(buffer: PacketByteBuf, entry: T)

    /**
     * Attempts to decode this entry, if null it will be skipped.
     * Any errors that result in a null entry should be logged.
     *
     * @param buffer The [PacketByteBuf] being decoded from.
     * @return The entry of type [T] if successfully decoded.
     */
    abstract fun decodeEntry(buffer: PacketByteBuf): T?

    /**
     * Synchronizes the final product the final product with the backing registry.
     *
     * @param entries The processed entries.
     */
    abstract fun synchronizeDecoded(entries: Map<Identifier, T>)

}