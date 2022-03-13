package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame

import net.minecraft.client.model.geom.ModelPart

interface QuadrupedFrame : ModelFrame {
    val foreLeftLeg: ModelPart
    val foreRightLeg: ModelPart
    val hindLeftLeg: ModelPart
    val hindRightLeg: ModelPart
}