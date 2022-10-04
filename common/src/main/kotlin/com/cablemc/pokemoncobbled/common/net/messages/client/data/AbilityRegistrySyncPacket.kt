/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.net.messages.client.data

import com.cablemc.pokemoncobbled.common.api.abilities.Abilities
import com.cablemc.pokemoncobbled.common.api.abilities.AbilityTemplate
import com.google.gson.reflect.TypeToken
import net.minecraft.util.Identifier

class AbilityRegistrySyncPacket : JsonDataRegistrySyncPacket<AbilityTemplate>(Abilities.gson, Abilities.all()) {
    override fun synchronizeDecoded(entries: Map<Identifier, AbilityTemplate>) {
        Abilities.reload(entries)
    }

    override fun type(): TypeToken<AbilityTemplate> = TypeToken.get(AbilityTemplate::class.java)
}