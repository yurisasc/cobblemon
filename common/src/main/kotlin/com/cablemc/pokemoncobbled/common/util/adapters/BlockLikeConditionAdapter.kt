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