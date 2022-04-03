package com.cablemc.pokemoncobbled.common.world.level.block

import com.cablemc.pokemoncobbled.common.world.level.block.grower.ApricornTreeGrower
import net.minecraft.world.level.block.SaplingBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature

class ApricornSaplingBlock(properties : Properties, color: String) : SaplingBlock(ApricornTreeGrower(color), properties)  {

    

}