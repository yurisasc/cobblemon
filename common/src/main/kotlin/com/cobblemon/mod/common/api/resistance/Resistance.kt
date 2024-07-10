/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.resistance

import net.minecraft.util.StringRepresentable

enum class Resistance(val showdownValue: Int) : StringRepresentable {

    NEUTRAL(0),
    SUPER_EFFECTIVE(1),
    NOT_VERY_EFFECTIVE(2),
    IMMUNE(3);

    override fun getSerializedName(): String = this.name

    companion object {
        @JvmStatic
        val CODEC = StringRepresentable.fromEnum(::values)
    }
}