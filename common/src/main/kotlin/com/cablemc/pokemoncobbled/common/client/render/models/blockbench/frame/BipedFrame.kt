package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame

import net.minecraft.client.model.ModelPart

interface BipedFrame : ModelFrame {
    val leftLeg: ModelPart
    val rightLeg: ModelPart
}