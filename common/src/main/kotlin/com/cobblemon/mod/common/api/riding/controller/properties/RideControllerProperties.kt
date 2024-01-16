/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.riding.controller.properties

import com.cobblemon.mod.common.api.net.Decodable
import com.cobblemon.mod.common.api.net.Encodable
import com.cobblemon.mod.common.api.reference.Reference
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.util.JsonSerializer

abstract class RideControllerProperties : Encodable, Decodable {

    protected abstract var identifier: Identifier

    abstract fun toAccessibleProperties(): Map<RideControllerPropertyKey<*>, Reference<*>>

    override fun decode(buffer: PacketByteBuf) {
        this.identifier = buffer.readIdentifier()
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeIdentifier(this.identifier)
    }

}