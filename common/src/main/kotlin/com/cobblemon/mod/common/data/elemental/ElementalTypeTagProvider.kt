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
import com.cobblemon.mod.common.data.CobblemonRegistryTagsProvider
import com.cobblemon.mod.common.mixin.accessor.TagsProviderAccessor
import com.cobblemon.mod.common.registry.CobblemonRegistries
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.TagsProvider
import java.util.concurrent.CompletableFuture

class ElementalTypeTagProvider(
    packOutput: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>
) : CobblemonRegistryTagsProvider<ElementalType>(packOutput, CobblemonRegistries.ELEMENTAL_TYPE_KEY, lookupProvider) {

    override fun addTags(provider: HolderLookup.Provider) {
        this.tag(CobblemonElementalTypeTags.OFFICIAL)
            .addOptional(ElementalTypes.NORMAL.location())
            .addOptional(ElementalTypes.FIRE.location())
            .addOptional(ElementalTypes.WATER.location())
            .addOptional(ElementalTypes.GRASS.location())
            .addOptional(ElementalTypes.ELECTRIC.location())
            .addOptional(ElementalTypes.ICE.location())
            .addOptional(ElementalTypes.FIGHTING.location())
            .addOptional(ElementalTypes.POISON.location())
            .addOptional(ElementalTypes.GROUND.location())
            .addOptional(ElementalTypes.FLYING.location())
            .addOptional(ElementalTypes.PSYCHIC.location())
            .addOptional(ElementalTypes.BUG.location())
            .addOptional(ElementalTypes.ROCK.location())
            .addOptional(ElementalTypes.GHOST.location())
            .addOptional(ElementalTypes.DRAGON.location())
            .addOptional(ElementalTypes.DARK.location())
            .addOptional(ElementalTypes.STEEL.location())
            .addOptional(ElementalTypes.FAIRY.location())
        
        this.tag(CobblemonElementalTypeTags.INTRODUCED_IN_GENERATION_1)
            .addOptional(ElementalTypes.NORMAL.location())
            .addOptional(ElementalTypes.FIRE.location())
            .addOptional(ElementalTypes.WATER.location())
            .addOptional(ElementalTypes.GRASS.location())
            .addOptional(ElementalTypes.ELECTRIC.location())
            .addOptional(ElementalTypes.ICE.location())
            .addOptional(ElementalTypes.FIGHTING.location())
            .addOptional(ElementalTypes.POISON.location())
            .addOptional(ElementalTypes.GROUND.location())
            .addOptional(ElementalTypes.FLYING.location())
            .addOptional(ElementalTypes.PSYCHIC.location())
            .addOptional(ElementalTypes.BUG.location())
            .addOptional(ElementalTypes.ROCK.location())
            .addOptional(ElementalTypes.GHOST.location())
            .addOptional(ElementalTypes.DRAGON.location())
            .addOptional(ElementalTypes.STEEL.location())

        this.tag(CobblemonElementalTypeTags.INTRODUCED_IN_GENERATION_2)
            .addOptional(ElementalTypes.DARK.location())

        this.tag(CobblemonElementalTypeTags.INTRODUCED_IN_GENERATION_6)
            .addOptional(ElementalTypes.FAIRY.location())

        this.tag(CobblemonElementalTypeTags.DROWN_IMMUNE)
            .addOptional(ElementalTypes.WATER.location())

        this.tag(CobblemonElementalTypeTags.FALL_IMMUNE)
            .addOptional(ElementalTypes.FLYING.location())

        this.tag(CobblemonElementalTypeTags.FIRE_IMMUNE)
            .addOptional(ElementalTypes.FIRE.location())

        this.tag(CobblemonElementalTypeTags.THUNDER_IMMUNE)
            .addOptional(ElementalTypes.ELECTRIC.location())
            .addOptional(ElementalTypes.GROUND.location())
    }
}