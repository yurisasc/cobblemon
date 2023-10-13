/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.util.codec

import com.mojang.serialization.Codec

fun <T> setCodec(elementCodec: Codec<T>): Codec<Set<T>> = elementCodec.listOf().xmap({ list -> list.toHashSet() }, { set -> set.toMutableList() })