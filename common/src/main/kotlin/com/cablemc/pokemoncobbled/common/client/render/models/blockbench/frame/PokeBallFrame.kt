package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame

import net.minecraft.client.model.ModelPart

interface PokeBallFrame : ModelFrame {
    val subRoot: ModelPart
    val lid: ModelPart
}