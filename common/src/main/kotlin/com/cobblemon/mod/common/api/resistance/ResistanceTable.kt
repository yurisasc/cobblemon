/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.resistance

import com.cobblemon.mod.common.api.effect.Effect
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult

class ResistanceMap(private val map: Map<Effect, Resistance>) : Map<Effect, Resistance> {

    override val entries: Set<Map.Entry<Effect, Resistance>>
        get() = this.map.entries
    override val keys: Set<Effect>
        get() = this.map.keys
    override val size: Int
        get() = this.map.size
    override val values: Collection<Resistance>
        get() = this.map.values

    override fun isEmpty(): Boolean = this.map.isEmpty()

    override fun get(key: Effect): Resistance? = this.map[key]

    override fun containsValue(value: Resistance): Boolean = this.map.containsValue(value)

    override fun containsKey(key: Effect): Boolean = this.map.containsKey(key)

    companion object {

        private val ENTRY_CODEC = Codec.pair(
            Effect.CODEC.fieldOf("effect").codec(),
            Resistance.CODEC.fieldOf("resistance").codec()
        )

        @JvmStatic
        val CODEC: Codec<ResistanceMap> = Codec.list(ENTRY_CODEC)
            .comapFlatMap(
                { list ->
                    DataResult.success(ResistanceMap(list.associate { it.first to it.second }))
                },
                { it.entries.map { entry -> Pair(entry.key, entry.value) } }
            )

    }
}