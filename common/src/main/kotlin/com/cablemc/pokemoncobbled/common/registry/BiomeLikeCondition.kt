package com.cablemc.pokemoncobbled.common.registry

import com.cablemc.pokemoncobbled.common.api.conditional.RegistryLikeCondition
import com.cablemc.pokemoncobbled.common.api.conditional.RegistryLikeIdentifierCondition
import com.cablemc.pokemoncobbled.common.api.conditional.RegistryLikeTagCondition
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