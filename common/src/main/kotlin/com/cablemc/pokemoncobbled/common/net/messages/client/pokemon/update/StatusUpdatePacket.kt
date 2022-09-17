/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update

import com.cablemc.pokemoncobbled.common.api.pokemon.status.Statuses
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.pokemon.status.PersistentStatus
import net.minecraft.util.Identifier

class StatusUpdatePacket() : StringUpdatePacket() {
    constructor(pokemon: Pokemon, value: String): this() {
        this.setTarget(pokemon)
        this.value = value
    }

    override fun set(pokemon: Pokemon, value: String) {
        if (value.isEmpty()) {
            pokemon.status = null
        } else {
            val status = Statuses.getStatus(Identifier(value))
            if (status != null && status is PersistentStatus) {
                pokemon.applyStatus(status)
            }
        }
    }
}