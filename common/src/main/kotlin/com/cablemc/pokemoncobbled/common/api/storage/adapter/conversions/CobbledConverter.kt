/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.api.storage.adapter.conversions

import com.cablemc.pokemoncobbled.common.api.storage.adapter.CobbledAdapter
import com.cablemc.pokemoncobbled.common.api.storage.party.PlayerPartyStore
import com.cablemc.pokemoncobbled.common.api.storage.pc.PCStore
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.nbt.NbtCompound
import java.nio.file.Path
import java.util.UUID
import kotlin.io.path.exists

interface CobbledConverter<S> : CobbledAdapter<S> {

    fun root(): Path

    fun exists(target: Path): Boolean {
        return target.exists()
    }

    fun party(user: UUID, nbt: NbtCompound): PlayerPartyStore

    fun pc(user: UUID, nbt: NbtCompound): PCStore

    fun translate(nbt: NbtCompound): Pokemon

}