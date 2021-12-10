package com.cablemc.pokemoncobbled.client.render.models.blockbench

import com.cablemc.pokemoncobbled.client.render.models.blockbench.additives.PosedAdditiveAnimation
import com.cablemc.pokemoncobbled.client.render.models.blockbench.animation.PoseTransitionAnimation
import com.cablemc.pokemoncobbled.client.render.models.blockbench.animation.StatefulAnimation
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pose.Pose
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.mod.PokemonCobbledMod.LOGGER
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
    var currentModel: PoseableEntityModel<T>? = null
    var currentPose: PoseType? = null
    val statefulAnimations: MutableList<StatefulAnimation<T, *>> = mutableListOf()
    val additives: MutableList<PosedAdditiveAnimation<T>> = mutableListOf()
    var idling: Boolean = true
    var animationTick = 0F

    fun getPose(): PoseType? {
        return currentPose
    }

    fun transitionPose(toPoseType: PoseType, durationTicks: Int = 20) {
        val model = currentModel ?: run {
            currentPose = PoseType.NONE // Bad
            return
        }

        val beforePose = model.poses[currentPose ?: PoseType.NONE]
            ?: Pose(PoseType.NONE, { true }, emptyArray(), emptyArray())
        val afterPose = model.poses[toPoseType]
            ?: run {
                LOGGER.error("Tried transitioning ${model::class.java} to pose type $toPoseType but there is no registered pose of that type.")
                return
            }

        val animation = PoseTransitionAnimation(model, beforePose, afterPose, durationTicks)
        statefulAnimations.add(animation)
    }

    fun setPose(pose: PoseType) {
        currentPose = pose
    }

    fun applyAdditives(entity: T, model: PoseableEntityModel<T>) {
        additives.removeIf { !it.run(entity, model) }
    }
}