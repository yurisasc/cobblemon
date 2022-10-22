/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.registry

import com.cablemc.pokemod.common.api.conditional.RegistryLikeIdentifierCondition
import com.cablemc.pokemod.common.api.conditional.RegistryLikeTagCondition
import net.minecraft.block.Block
import net.minecraft.tag.TagKey
import net.minecraft.util.Identifier

/**
 * A tag condition for blocks. Built off of [RegistryLikeTagCondition].
 *
 * @author Hiroku
 * @since July 15th, 2022
 */
class BlockTagCondition(tag: TagKey<Block>) : RegistryLikeTagCondition<Block>(tag)
/**
 * An identifier condition for blocks. Built off of [RegistryLikeIdentifierCondition].
 *
 * @author Hiroku
 * @since July 15th, 2022
 */
class BlockIdentifierCondition(identifier: Identifier) : RegistryLikeIdentifierCondition<Block>(identifier)