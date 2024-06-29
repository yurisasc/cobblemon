/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.messages.client.data

import com.cobblemon.mod.common.api.net.NetworkPacket
import io.netty.buffer.Unpooled
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.RegistryByteBuf

abstract class DataRegistrySyncPacket<T, N : NetworkPacket<N>>(private val registryEntries: Collection<T>) : NetworkPacket<N> {

    var buffer: RegistryByteBuf? = null
    internal val entries = arrayListOf<T>()

    override fun encode(buffer: RegistryByteBuf) {
        val newBuffer = RegistryByteBuf(Unpooled.buffer(), buffer.registryManager)
        newBuffer.writeCollection(registryEntries) { _, entry -> encodeEntry(newBuffer, entry) }
        buffer.writeInt(newBuffer.readableBytes())
        buffer.writeBytes(newBuffer)
    }

    internal fun decodeBuffer(buffer: RegistryByteBuf) {
        val size = buffer.readInt()
        val newBuffer = RegistryByteBuf(buffer.readBytes(size), buffer.registryManager)
        this.buffer = newBuffer
    }

    /**
     * Encodes an entry of type [T] to the [PacketByteBuf].
     *
     * @param buffer The [PacketByteBuf] being encoded to.
     * @param entry The entry of type [T].
     */
    abstract fun encodeEntry(buffer: RegistryByteBuf, entry: T)

    /**
     * Attempts to decode this entry, if null it will be skipped.
     * Any errors that result in a null entry should be logged.
     *
     * @param buffer The [PacketByteBuf] being decoded from.
     * @return The entry of type [T].
     */
    abstract fun decodeEntry(buffer: RegistryByteBuf): T?

    /**
     * Synchronizes the final product the final product with the backing registry.
     *
     * @param entries The processed entries.
     */
    abstract fun synchronizeDecoded(entries: Collection<T>)

}