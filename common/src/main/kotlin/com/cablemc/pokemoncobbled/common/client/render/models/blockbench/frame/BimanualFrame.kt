package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame

import net.minecraft.client.model.ModelPart

/** Two arms */
interface BimanualFrame : ModelFrame {
    val leftArm: ModelPart
    val rightArm: ModelPart
}