/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.util.adapters

import com.cablemc.pokemoncobbled.common.api.conditional.RegistryLikeAdapter
import com.cablemc.pokemoncobbled.common.api.conditional.RegistryLikeIdentifierCondition
import com.cablemc.pokemoncobbled.common.api.conditional.RegistryLikeTagCondition
import com.cablemc.pokemoncobbled.common.registry.BlockIdentifierCondition
import com.cablemc.pokemoncobbled.common.registry.BlockTagCondition
import net.minecraft.block.Block
import net.minecraft.util.registry.Registry

object BlockLikeConditionAdapter : RegistryLikeAdapter<Block> {
    override val registryLikeConditions = mutableListOf(
        RegistryLikeTagCondition.resolver(Registry.BLOCK_KEY, ::BlockTagCondition),
        RegistryLikeIdentifierCondition.resolver(::BlockIdentifierCondition)
    )
}