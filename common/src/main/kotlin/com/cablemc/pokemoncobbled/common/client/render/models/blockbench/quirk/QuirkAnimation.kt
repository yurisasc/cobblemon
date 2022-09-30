package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.quirk

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.PoseableEntityState
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.StatefulAnimation

class QuirkAnimation(
    val name: String,
    val state: PoseableEntityState<*>, val animation: StatefulAnimation<*, *>) {

}