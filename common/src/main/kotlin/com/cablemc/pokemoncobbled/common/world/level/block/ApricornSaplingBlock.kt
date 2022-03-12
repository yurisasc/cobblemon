package com.cablemc.pokemoncobbled.common.world.level.block

import com.cablemc.pokemoncobbled.common.world.level.block.grower.ApricornTreeGrower
import net.minecraft.world.level.block.SaplingBlock
import net.minecraft.world.level.block.state.BlockBehaviour

class ApricornSaplingBlock(properties : BlockBehaviour.Properties) : SaplingBlock(ApricornTreeGrower(), properties)  {

    

}