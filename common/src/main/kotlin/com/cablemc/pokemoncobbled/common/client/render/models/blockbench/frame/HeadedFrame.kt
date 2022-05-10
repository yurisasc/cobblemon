package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.SingleBoneLookAnimation
import net.minecraft.client.model.ModelPart
import net.minecraft.entity.Entity

interface HeadedFrame : ModelFrame {
    val head: ModelPart

    fun <T : Entity> singleBoneLook() = SingleBoneLookAnimation<T>(this)
}