package com.cablemc.pokemoncobbled.common.entity.animation

import com.cablemc.pokemoncobbled.client.render.pokeball.animation.ModelAnimation
import net.minecraft.client.model.EntityModel
import net.minecraft.world.entity.Entity

data class AnimationRegistry<T: Entity, A: ModelAnimation<out EntityModel<T>>>(
    /** Determines if the animation can run or not. For example, a swim animation should only run if the entity is in water. */
    val predicate: (T) -> Boolean,
    val animation: A
)