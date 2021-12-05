package com.cablemc.pokemoncobbled.client.render.models.blockbench.frame

import net.minecraft.client.model.geom.ModelPart

interface HeadedFrame : ModelFrame {
    val head: ModelPart
}