package com.cablemc.pokemoncobbled.client.render.models.blockbench.animation

import com.cablemc.pokemoncobbled.client.render.models.blockbench.frame.ModelFrame
import net.minecraft.world.entity.Entity

interface StatefulAnimation<T : Entity, F : ModelFrame> {
    val frameClass: Class<F>
    fun run(entity: T, frame: F): Boolean
}