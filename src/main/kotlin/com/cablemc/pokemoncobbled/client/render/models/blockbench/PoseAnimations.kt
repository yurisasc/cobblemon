package com.cablemc.pokemoncobbled.client.render.models.blockbench

import com.cablemc.pokemoncobbled.client.render.models.blockbench.animation.StatefulAnimation
import com.cablemc.pokemoncobbled.client.render.models.blockbench.frame.ModelFrame
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pose.Pose
import net.minecraft.world.entity.Entity

object PoseAnimations {
    class PoseAnimationSet<T : Entity, F : ModelFrame, P : Pose<T, F>>(
        val entityClass: Class<T>,
        val frameClass: Class<F>,
        val animations: MutableList<StatefulAnimation<T, F>>
    )

    val poseAnimations = mutableMapOf<Pose<*, *>, MutableList<PoseAnimationSet<*, *, *>>>()

    inline fun <reified T : Entity, reified F : ModelFrame, P : Pose<T, F>> registerPoseAnimations(pose: P, vararg animations: StatefulAnimation<T, F>) {
        poseAnimations
            .getOrPut(pose) { mutableListOf() }
            .add(PoseAnimationSet<T, F, P>(T::class.java, F::class.java, animations.toMutableList()))
    }
}