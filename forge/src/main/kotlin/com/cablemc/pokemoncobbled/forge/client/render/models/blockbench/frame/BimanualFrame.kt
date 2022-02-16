package com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.frame

import net.minecraft.client.model.geom.ModelPart

/** Two arms */
interface BimanualFrame : ModelFrame {
    val leftArm: ModelPart
    val rightArm: ModelPart
}