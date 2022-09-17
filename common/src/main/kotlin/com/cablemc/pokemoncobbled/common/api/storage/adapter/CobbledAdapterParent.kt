/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.api.storage.adapter

import com.cablemc.pokemoncobbled.common.api.storage.PokemonStore
import com.cablemc.pokemoncobbled.common.api.storage.StorePosition
import java.util.*

/**
 * Provides a generic layer for adapters which are expected to allow for children
 *
 * @author NickImpact
 * @since August 22nd, 2022
 */
abstract class CobbledAdapterParent<S> : CobbledAdapter<S> {

    val children: MutableList<CobbledAdapter<*>> = mutableListOf()
    fun with(vararg children: CobbledAdapter<*>) : CobbledAdapter<S> {
        this.children.addAll(children)
        return this
    }

    override fun <E : StorePosition, T : PokemonStore<E>> load(storeClass: Class<T>, uuid: UUID): T? {
        val result = this.provide(storeClass, uuid)
        if (result == null) {
            for (child in this.children) {
                child.load(storeClass, uuid).let { return@load it }
            }
        }

        return null
    }

    abstract fun <E : StorePosition, T : PokemonStore<E>> provide(storeClass: Class<T>, uuid: UUID): T?

}