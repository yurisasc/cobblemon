package com.cablemc.pokemoncobbled.client.render.models.blockbench

import com.cablemc.pokemoncobbled.client.render.models.blockbench.additives.PosedAdditiveAnimation
import com.cablemc.pokemoncobbled.client.render.models.blockbench.animation.StatefulAnimation
import com.cablemc.pokemoncobbled.client.render.models.blockbench.frame.ModelFrame
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pose.Pose
import net.minecraft.world.entity.Entity

abstract class PoseableEntityState<T : Entity> {
    var currentPose: Pose<T, *>? = null
    val statefulAnimations: MutableList<out StatefulAnimation<T, *>> = mutableListOf()
    val additives: MutableList<PosedAdditiveAnimation<T>> = mutableListOf()

    fun <F : ModelFrame> getPose(): Pose<T, F>? {
        currentPose?.let {
            return it as Pose<T, F>
        } ?: return null
    }

    fun <F : ModelFrame> setPose(pose: Pose<T, F>) {
        currentPose = pose
    }

    fun applyAdditives(entity: T, model: PoseableEntityModel<T, *>) {
        additives.removeIf { !it.run(entity, model.frame) }
    }
}