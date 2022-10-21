/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.util.collections
class ImmutableArray<T>(private vararg val values: T) {

    operator fun get(index: Int) = values[index]

}

inline fun <reified T> immutableArrayOf(vararg values: T) = ImmutableArray(*values)