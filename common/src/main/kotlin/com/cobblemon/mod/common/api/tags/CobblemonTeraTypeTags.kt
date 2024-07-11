/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.tags

import com.cobblemon.mod.common.registry.CobblemonRegistries
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.tags.TagKey

@Suppress("unused")
object CobblemonTeraTypeTags {

    /**
     * Contains the tera types present in the mains series games.
     */
    @JvmStatic
    val OFFICIAL = this.create("official")

    /**
     * Intended to be used by third party contains custom tera types not present in the mains series games.
     * There is no guarantee third party authors will adhere to this principle.
     */
    @JvmStatic
    val CUSTOM = this.create("custom")

    /**
     * Tera types that comes from an elemental type.
     */
    @JvmStatic
    val ELEMENTAL_TYPE_BASED = this.create("elemental_type_based")

    /**
     * Tera types exclusive to the gimmick.
     */
    @JvmStatic
    val GIMMICK_ONLY = this.create("gimmick_only")

    /**
     * Tera types exclusive to the gimmick.
     */
    @JvmStatic
    val LEGAL_AS_STATIC = this.create("legal_as_static")

    private fun create(path: String) = TagKey.create(CobblemonRegistries.TERA_TYPE_KEY, cobblemonResource(path))

}