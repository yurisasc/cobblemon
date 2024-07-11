/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.resistance

import com.cobblemon.mod.common.api.effect.Effect

interface Resistible {

    fun effectivenessAgainst(other: Resistible): Resistance = other.resistanceTo(this.asEffect())

    fun resistanceTo(effect: Effect): Resistance

    fun asEffect(): Effect

}