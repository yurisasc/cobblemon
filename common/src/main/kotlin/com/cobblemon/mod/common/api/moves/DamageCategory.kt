/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.moves

import com.cobblemon.mod.common.util.lang
import com.mojang.serialization.Codec
import net.minecraft.text.MutableText
import net.minecraft.util.StringIdentifiable

/**
 * Represents the damage category of a [MoveTemplate]/[Move].
 *
 * @see MoveTemplate
 * @see Move
 */
enum class DamageCategory : StringIdentifiable {

    PHYSICAL,
    SPECIAL,
    STATUS;

    override fun asString(): String = this.name

    /**
     * Resolves the display name of this damage category.
     *
     * @return The generated text.
     */
    fun displayName(): MutableText = lang("move.category.${this.name.lowercase()}")

    companion object {

        val CODEC: Codec<DamageCategory> = StringIdentifiable.createCodec(DamageCategory::values)

    }

}