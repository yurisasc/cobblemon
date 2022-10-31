/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.registry

import com.cablemc.pokemod.common.api.conditional.RegistryLikeCondition
import com.cablemc.pokemod.common.api.conditional.RegistryLikeIdentifierCondition
import com.cablemc.pokemod.common.api.conditional.RegistryLikeTagCondition
import net.minecraft.item.Item
import net.minecraft.tag.TagKey
import net.minecraft.util.Identifier

/**
 * A [RegistryLikeCondition] that expects a [TagKey] attached to the [Item] registry.
 *
 * @property tag The tag to check for the item to match.
 *
 * @author Licious
 * @since October 28th, 2022
 */
class ItemTagCondition(tag: TagKey<Item>) : RegistryLikeTagCondition<Item>(tag)

/**
 * A [RegistryLikeCondition] that expects an [Identifier] to match.
 *
 * @property identifier The identifier for the item being referenced.
 *
 * @author Licious
 * @since October 28th, 2022
 */
class ItemIdentifierCondition(identifier: Identifier) : RegistryLikeIdentifierCondition<Item>(identifier)