/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.net.messages.client.data

import com.cablemc.pokemod.common.api.abilities.Abilities
import com.cablemc.pokemod.common.api.abilities.AbilityTemplate
import com.cablemc.pokemod.common.util.pokemodResource
import net.minecraft.network.PacketByteBuf

class AbilityRegistrySyncPacket : DataRegistrySyncPacket<AbilityTemplate>(Abilities.all()) {
    override fun encodeEntry(buffer: PacketByteBuf, entry: AbilityTemplate) {
        buffer.writeString(entry.name)
        buffer.writeText(entry.displayName)
        buffer.writeText(entry.description)
    }

    override fun decodeEntry(buffer: PacketByteBuf): AbilityTemplate? {
        return AbilityTemplate(
            name = buffer.readString(),
            displayName = buffer.readText().copy(),
            description= buffer.readText().copy()
        )
    }

    override fun synchronizeDecoded(entries: Collection<AbilityTemplate>) {
        Abilities.reload(entries.associateBy { pokemodResource(it.name.lowercase()) })
    }

}