package com.cablemc.pokemoncobbled.common.registry

import com.cablemc.pokemoncobbled.common.api.conditional.RegistryLikeIdentifierCondition
import com.cablemc.pokemoncobbled.common.api.conditional.RegistryLikeTagCondition
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