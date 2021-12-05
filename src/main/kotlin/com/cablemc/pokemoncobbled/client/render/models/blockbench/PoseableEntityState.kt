package com.cablemc.pokemoncobbled.client.render.models.blockbench

import com.cablemc.pokemoncobbled.client.render.models.blockbench.additives.PosedAdditiveAnimation
import com.cablemc.pokemoncobbled.client.render.models.blockbench.animation.StatefulAnimation
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pose.PoseType
import net.minecraft.world.entity.Entity

/**
 * Represents the entity-specific state for a poseable model. The implementation is responsible for
 * handling all of the state for an entity's model, and needs to be conscious of the fact that the
 * model may change without this state changing.
 *
 * @author Hiroku
 * @since December 5th, 2021
 */
abstract class PoseableEntityState<T : Entity> {
    var currentPose: PoseType? = null
    val statefulAnimations: MutableList<out StatefulAnimation<T, *>> = mutableListOf()
    val additives: MutableList<PosedAdditiveAnimation<T>> = mutableListOf()
    var idling: Boolean = true

    fun getPose(): PoseType? {
        return currentPose
    }

    fun setPose(pose: PoseType) {
        currentPose = pose
    }

    fun applyAdditives(entity: T, model: PoseableEntityModel<T>) {
        additives.removeIf { !it.run(entity, model) }
    }
}