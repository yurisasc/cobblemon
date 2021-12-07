package com.cablemc.pokemoncobbled.client.render.models.blockbench.animation

import com.cablemc.pokemoncobbled.client.render.models.blockbench.PoseableEntityModel
import com.cablemc.pokemoncobbled.client.render.models.blockbench.frame.ModelFrame
import net.minecraft.world.entity.Entity

/**
 * An animation that requires entity state. It is able to prevent some idle
 * animations, usually on the basis of what [ModelFrame] class the animation
 * uses.
 *
 * @author Hiroku
 * @since December 5th, 2021
 */
abstract class StatefulAnimation<T : Entity, F : ModelFrame>(val frame: F) {
    /**
     * Whether this animation should prevent the given idle animation from occurring.
     *
     * This is for cases where this animation and the idle animation work on the same parts
     * of the model and would conflict.
     */
    abstract fun preventsIdle(entity: T, idleAnimation: StatelessAnimation<T, *>): Boolean
    /** Runs the animation. You can access the frame from the class properties. */
    abstract fun run(entity: T, model: PoseableEntityModel<T>): Boolean
}