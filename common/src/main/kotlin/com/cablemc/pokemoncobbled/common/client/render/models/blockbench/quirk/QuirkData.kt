package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.quirk

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.PoseableEntityModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.PoseableEntityState
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.StatefulAnimation
import net.minecraft.entity.Entity

open class QuirkData<T : Entity>(val name: String) {
    val animations = mutableListOf<StatefulAnimation<T, *>>()

    open fun run(entity: T?, model: PoseableEntityModel<T>, state: PoseableEntityState<T>, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float, headYaw: Float, headPitch: Float) {
        animations.removeIf { !it.run(entity, model, state, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch) }
    }
}