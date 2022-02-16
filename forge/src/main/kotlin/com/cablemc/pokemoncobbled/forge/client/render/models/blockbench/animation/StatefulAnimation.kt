package com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.animation

import com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.PoseableEntityModel
import com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.frame.ModelFrame
import net.minecraft.world.entity.Entity

/**
 * An animation that requires entity state. It is able to prevent some idle
 * animations, usually on the basis of what [ModelFrame] class the animation
 * uses.
 *
 * @author Hiroku
 * @since December 5th, 2021
 */
interface StatefulAnimation<T : Entity, F : ModelFrame> {
    /**
     * Whether this animation should prevent the given idle animation from occurring.
     *
     * This is for cases where this animation and the idle animation work on the same parts
     * of the model and would conflict.
     */
    fun preventsIdle(entity: T, idleAnimation: StatelessAnimation<T, *>): Boolean
    /** Runs the animation. You can check that the model fits a particular frame. Returns true if the animation should continue. */
    fun run(entity: T, model: PoseableEntityModel<T>): Boolean
}