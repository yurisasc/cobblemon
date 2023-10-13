/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.types.hiddenpower

import com.mojang.serialization.Codec
import net.minecraft.util.StringIdentifiable

/**
 * Represents the odd or even IV value requirement for hidden power.
 */
enum class IvCondition : StringIdentifiable {

    EVEN,
    ODD;

    override fun asString(): String = this.name

    /**
     * Checks if the given IV value respects the condition.
     *
     * @param ivValue The value being checked.
     * @return true if the value respects the condition.
     */
    fun fits(ivValue: Int): Boolean = when (this) {
        EVEN -> ivValue % 2 == 0
        ODD -> ivValue % 2 != 0
    }

    companion object {

        val CODEC: Codec<IvCondition> = StringIdentifiable.createCodec(IvCondition::values)

    }

}