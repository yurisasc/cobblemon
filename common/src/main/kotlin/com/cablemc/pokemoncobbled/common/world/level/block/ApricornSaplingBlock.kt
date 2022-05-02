package com.cablemc.pokemoncobbled.common.world.level.block

import com.cablemc.pokemoncobbled.common.world.level.block.grower.ApricornTreeGrower
import net.minecraft.block.SaplingBlock

class ApricornSaplingBlock(properties : Settings, color: String) : SaplingBlock(ApricornTreeGrower(color), properties)