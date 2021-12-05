package com.cablemc.pokemoncobbled.client.render.models.blockbench.pose

import com.cablemc.pokemoncobbled.client.render.models.blockbench.animation.StatelessAnimation
import com.cablemc.pokemoncobbled.client.render.models.blockbench.frame.ModelFrame
import net.minecraft.world.entity.Entity

class RegisteredPose<T : Entity, F : ModelFrame>(
    val pose: Pose<T, F>,
    val frame: F,
    val idleAnimations: Array<out StatelessAnimation<T, F>>,
    val transformedParts: Array<TransformedModelPart>
)