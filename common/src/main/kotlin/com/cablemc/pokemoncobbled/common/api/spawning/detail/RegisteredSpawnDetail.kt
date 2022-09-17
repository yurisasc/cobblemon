/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.api.spawning.detail

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement

/**
 * A [SpawnDetail] implementation that has been registered.
 *
 * @author Hiroku
 * @since January 31st, 2022
 */
class RegisteredSpawnDetail<T : SpawnDetail>(
    val detailClass: Class<T>
) {
    fun deserializeDetail(element: JsonElement, ctx: JsonDeserializationContext): T = ctx.deserialize(element, detailClass)
}