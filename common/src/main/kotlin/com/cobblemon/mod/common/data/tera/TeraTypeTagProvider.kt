/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.data.tera

import com.cobblemon.mod.common.api.tags.CobblemonTeraTypeTags
import com.cobblemon.mod.common.api.types.tera.TeraType
import com.cobblemon.mod.common.api.types.tera.TeraTypes
import com.cobblemon.mod.common.registry.CobblemonRegistries
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.TagsProvider
import net.minecraft.resources.ResourceKey
import java.util.concurrent.CompletableFuture

class TeraTypeTagProvider(
    packOutput: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>
) : TagsProvider<TeraType>(packOutput, CobblemonRegistries.TERA_TYPE_KEY, lookupProvider) {
    override fun addTags(provider: HolderLookup.Provider) {
        this.tag(CobblemonTeraTypeTags.OFFICIAL)
            .add(*this.elementalTypes())
            .add(*this.gimmick())

        this.tag(CobblemonTeraTypeTags.ELEMENTAL_TYPE_BASED)
            .add(*this.elementalTypes())

        this.tag(CobblemonTeraTypeTags.GIMMICK_ONLY)
            .add(*this.gimmick())

        this.tag(CobblemonTeraTypeTags.LEGAL_AS_STATIC)
            .addTag(CobblemonTeraTypeTags.ELEMENTAL_TYPE_BASED)
    }

    private fun elementalTypes(): Array<ResourceKey<TeraType>> = arrayOf(
        TeraTypes.NORMAL,
        TeraTypes.FIRE,
        TeraTypes.WATER,
        TeraTypes.GRASS,
        TeraTypes.ELECTRIC,
        TeraTypes.ICE,
        TeraTypes.FIGHTING,
        TeraTypes.POISON,
        TeraTypes.GROUND,
        TeraTypes.FLYING,
        TeraTypes.PSYCHIC,
        TeraTypes.BUG,
        TeraTypes.ROCK,
        TeraTypes.GHOST,
        TeraTypes.DRAGON,
        TeraTypes.DARK,
        TeraTypes.STEEL,
        TeraTypes.FAIRY,
    )

    private fun gimmick(): Array<ResourceKey<TeraType>> = arrayOf(
        TeraTypes.STELLAR,
    )

}