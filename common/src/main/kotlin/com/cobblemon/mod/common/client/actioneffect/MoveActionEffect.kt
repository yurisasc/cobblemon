package com.cobblemon.mod.common.client.actioneffect

import com.cobblemon.mod.common.api.moves.animations.ActionEffect
import com.cobblemon.mod.common.api.moves.animations.MoveAnimationKeyframe

class MoveActionEffect(
    val moveName: String,
    val pokemon: Set<String>? = null,
    val priority: Int = 1,
    val actionEffect: ActionEffect
) {
}