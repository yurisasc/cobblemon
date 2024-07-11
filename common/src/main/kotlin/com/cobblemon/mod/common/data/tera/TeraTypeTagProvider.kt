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
import java.util.concurrent.CompletableFuture

class TeraTypeTagProvider(
    packOutput: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>
) : TagsProvider<TeraType>(packOutput, CobblemonRegistries.TERA_TYPE_KEY, lookupProvider) {
    override fun addTags(provider: HolderLookup.Provider) {
        this.tag(CobblemonTeraTypeTags.OFFICIAL)
            .addOptional(TeraTypes.NORMAL.location())
            .addOptional(TeraTypes.FIRE.location())
            .addOptional(TeraTypes.WATER.location())
            .addOptional(TeraTypes.GRASS.location())
            .addOptional(TeraTypes.ELECTRIC.location())
            .addOptional(TeraTypes.ICE.location())
            .addOptional(TeraTypes.FIGHTING.location())
            .addOptional(TeraTypes.POISON.location())
            .addOptional(TeraTypes.GROUND.location())
            .addOptional(TeraTypes.FLYING.location())
            .addOptional(TeraTypes.PSYCHIC.location())
            .addOptional(TeraTypes.BUG.location())
            .addOptional(TeraTypes.ROCK.location())
            .addOptional(TeraTypes.GHOST.location())
            .addOptional(TeraTypes.DRAGON.location())
            .addOptional(TeraTypes.DARK.location())
            .addOptional(TeraTypes.STEEL.location())
            .addOptional(TeraTypes.FAIRY.location())
            .addOptional(TeraTypes.STELLAR.location())

        this.tag(CobblemonTeraTypeTags.ELEMENTAL_TYPE_BASED)
            .addOptional(TeraTypes.NORMAL.location())
            .addOptional(TeraTypes.FIRE.location())
            .addOptional(TeraTypes.WATER.location())
            .addOptional(TeraTypes.GRASS.location())
            .addOptional(TeraTypes.ELECTRIC.location())
            .addOptional(TeraTypes.ICE.location())
            .addOptional(TeraTypes.FIGHTING.location())
            .addOptional(TeraTypes.POISON.location())
            .addOptional(TeraTypes.GROUND.location())
            .addOptional(TeraTypes.FLYING.location())
            .addOptional(TeraTypes.PSYCHIC.location())
            .addOptional(TeraTypes.BUG.location())
            .addOptional(TeraTypes.ROCK.location())
            .addOptional(TeraTypes.GHOST.location())
            .addOptional(TeraTypes.DRAGON.location())
            .addOptional(TeraTypes.DARK.location())
            .addOptional(TeraTypes.STEEL.location())
            .addOptional(TeraTypes.FAIRY.location())

        this.tag(CobblemonTeraTypeTags.GIMMICK_ONLY)
            .addOptional(TeraTypes.STELLAR.location())

        this.tag(CobblemonTeraTypeTags.LEGAL_AS_STATIC)
            .addOptionalTag(CobblemonTeraTypeTags.ELEMENTAL_TYPE_BASED.location)
    }

}