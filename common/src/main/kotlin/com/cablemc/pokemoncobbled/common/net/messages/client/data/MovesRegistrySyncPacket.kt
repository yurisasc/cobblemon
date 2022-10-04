/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.net.messages.client.data

import com.cablemc.pokemoncobbled.common.api.moves.MoveTemplate
import com.cablemc.pokemoncobbled.common.api.moves.Moves
import com.google.gson.reflect.TypeToken
import net.minecraft.util.Identifier

class MovesRegistrySyncPacket : JsonDataRegistrySyncPacket<MoveTemplate>(Moves.gson, Moves.all()) {
    override fun synchronizeDecoded(entries: Map<Identifier, MoveTemplate>) {
        Moves.reload(entries)
    }

    override fun type(): TypeToken<MoveTemplate> = TypeToken.get(MoveTemplate::class.java)
}