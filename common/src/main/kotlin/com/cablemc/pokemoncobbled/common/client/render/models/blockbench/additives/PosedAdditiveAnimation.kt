package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.additives

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.PoseableEntityModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.ModelFrame
import net.minecraft.entity.Entity

/**
 * A freeform, stateful animation that can be applied to any model that
 * fits the entity type. In most cases you will want to check in the run
 * function whether the model is of the right [ModelFrame] to do what you
 * need.
 *
 * @author Hiroku
 * @since December 5th, 2021
 */
interface PosedAdditiveAnimation<T : Entity> {
    /** Runs the animation and returns true if the animation should continue. */
    fun run(entity: T, model: PoseableEntityModel<T>): Boolean
}