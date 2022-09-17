/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.api.events.net

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket

/**
 * Fired when a new packet is being initialized and a handler should be applied. Internal use only!
 *
 * @author Hiroku
 * @since November 27th, 2021
 */
class MessageBuiltEvent<T : NetworkPacket>(
    val clazz: Class<T>,
    val isToServer: Boolean,
    val messageBuilder: CobbledNetwork.PreparedMessage<T>
)