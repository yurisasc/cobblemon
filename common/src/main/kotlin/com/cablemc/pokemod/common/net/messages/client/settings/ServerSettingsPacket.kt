/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.net.messages.client.settings

import com.cablemc.pokemod.common.Pokemod
import com.cablemc.pokemod.common.api.net.NetworkPacket
import net.minecraft.network.PacketByteBuf

/**
 * A packet that will sync simple config settings to the client that shouldn't require to be data pack powered.
 *
 * @author Licious
 * @since September 25th, 2022
 */
class ServerSettingsPacket internal constructor() : NetworkPacket {

    var preventCompletePartyDeposit = false
        private set

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeBoolean(Pokemod.config.preventCompletePartyDeposit)
    }

    override fun decode(buffer: PacketByteBuf) {
        this.preventCompletePartyDeposit = buffer.readBoolean()
    }

}