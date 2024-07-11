/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.data.elemental

import com.cobblemon.mod.common.api.tags.CobblemonElementalTypeTags
import com.cobblemon.mod.common.api.types.ElementalType
import com.cobblemon.mod.common.api.types.ElementalTypes
import com.cobblemon.mod.common.registry.CobblemonRegistries
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.TagsProvider
import java.util.concurrent.CompletableFuture

class ElementalTypeTagProvider(
    packOutput: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>
) : TagsProvider<ElementalType>(packOutput, CobblemonRegistries.ELEMENTAL_TYPE_KEY, lookupProvider) {
    override fun addTags(provider: HolderLookup.Provider) {
        this.tag(CobblemonElementalTypeTags.OFFICIAL)
            .add(*ElementalTypes.keys().toTypedArray())
        
        this.tag(CobblemonElementalTypeTags.INTRODUCED_IN_GENERATION_1)
            .add(
                ElementalTypes.NORMAL,
                ElementalTypes.FIRE,
                ElementalTypes.WATER,
                ElementalTypes.GRASS,
                ElementalTypes.ELECTRIC,
                ElementalTypes.ICE,
                ElementalTypes.FIGHTING,
                ElementalTypes.POISON,
                ElementalTypes.GROUND,
                ElementalTypes.FLYING,
                ElementalTypes.PSYCHIC,
                ElementalTypes.BUG,
                ElementalTypes.ROCK,
                ElementalTypes.GHOST,
                ElementalTypes.DRAGON,
                ElementalTypes.DARK,
                ElementalTypes.STEEL,
            )

        this.tag(CobblemonElementalTypeTags.INTRODUCED_IN_GENERATION_6)
            .add(
                ElementalTypes.FAIRY,
            )

        this.tag(CobblemonElementalTypeTags.DROWN_IMMUNE)
            .add(
                ElementalTypes.WATER,
            )

        this.tag(CobblemonElementalTypeTags.FALL_IMMUNE)
            .add(
                ElementalTypes.FLYING,
            )

        this.tag(CobblemonElementalTypeTags.FIRE_IMMUNE)
            .add(
                ElementalTypes.FIRE,
            )

        this.tag(CobblemonElementalTypeTags.THUNDER_IMMUNE)
            .add(
                ElementalTypes.ELECTRIC,
                ElementalTypes.GROUND,
            )
    }
}