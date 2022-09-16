/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.client.render.models.blockbench

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.additives.PosedAdditiveAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.StatefulAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.ModelFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.Pose
import net.minecraft.entity.Entity

/**
 * Represents the entity-specific state for a poseable model. The implementation is responsible for
 * handling all the state for an entity's model, and needs to be conscious of the fact that the
 * model may change without this state changing.
 *
 * @author Hiroku
 * @since December 5th, 2021
 */
abstract class PoseableEntityState<T : Entity> {
    var currentModel: PoseableEntityModel<T>? = null
    var currentPose: String? = null
    val statefulAnimations: MutableList<StatefulAnimation<T, *>> = mutableListOf()
    val additives: MutableList<PosedAdditiveAnimation<T>> = mutableListOf()
    var animationSeconds = 0F
    var timeLastRendered = System.currentTimeMillis()

    fun isPosedIn(vararg poses: Pose<T, in ModelFrame>) = poses.any { it.poseName == currentPose }
    fun isNotPosedIn(vararg poses: Pose<T, in ModelFrame>) = poses.none { it.poseName == currentPose }

    fun preRender() {
        val now = System.currentTimeMillis()
        animationSeconds += (now - timeLastRendered) / 1000F
        timeLastRendered = now
    }

    fun getPose(): String? {
        return currentPose
    }

    fun setPose(pose: String) {
        currentPose = pose
        val model = currentModel
        if (model != null) {
            val poseImpl = model.getPose(pose) ?: return
            poseImpl.onTransitionedInto(this)
        }

    }

    fun applyAdditives(entity: T?, model: PoseableEntityModel<T>, state: PoseableEntityState<T>?) {
        additives.removeIf { !it.run(entity, model, state) }
    }
}