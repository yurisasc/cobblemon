package com.cablemc.pokemoncobbled.client.render.models.blockbench.additives

import com.cablemc.pokemoncobbled.client.render.models.blockbench.frame.ModelFrame
import net.minecraft.world.entity.Entity

interface PosedAdditiveAnimation<T : Entity> {
    fun run(entity: T, frame: ModelFrame): Boolean
}