/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.data

import com.cobblemon.mod.common.api.data.Identifiable
import com.mojang.serialization.Codec

interface DataExport<T> : Identifiable {

    fun codec(): Codec<T>

    fun value(): T

}