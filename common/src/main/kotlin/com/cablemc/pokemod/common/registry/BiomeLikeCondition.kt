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
import net.minecraft.tag.TagKey
import net.minecraft.util.Identifier
import net.minecraft.world.biome.Biome

/**
 * A [RegistryLikeCondition] that expects a [TagKey] attached to the [Biome] registry.
 *
 * @property tag The tag to check for the block to match.
 *
 * @author Licious
 * @since July 1st, 2022
 */
class BiomeTagCondition(tag: TagKey<Biome>) : RegistryLikeTagCondition<Biome>(tag)

/**
 * A [RegistryLikeCondition] that expects an [Identifier] to match.
 *
 * @property identifier The identifier for the block being referenced.
 *
 * @author Licious
 * @since July 1st, 2022
 */
class BiomeIdentifierCondition(identifier: Identifier) : RegistryLikeIdentifierCondition<Biome>(identifier)