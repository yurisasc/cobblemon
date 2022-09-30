package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.quirk

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.PoseableEntityState
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.StatefulAnimation
import com.cablemc.pokemoncobbled.common.util.math.random
import kotlin.random.Random
import net.minecraft.entity.Entity

class SimpleQuirk<T : Entity>(
    name: String,
    private val secondsBetweenOccurrences: Pair<Float, Float>,
    val condition: (state: PoseableEntityState<T>) -> Boolean ,
    val loopTimes: IntRange = 1..1,
    val animations: (state: PoseableEntityState<T>) -> Iterable<StatefulAnimation<T, *>>
) : ModelQuirk<T, SimpleQuirkData<T>>(name) {
    override fun createData(): SimpleQuirkData<T> = SimpleQuirkData(name)
    override fun tick(state: PoseableEntityState<T>, data: SimpleQuirkData<T>) {
        if (data.animations.isNotEmpty()) {
            return
        }

        if (!condition(state)) {
            return
        }

        if (data.remainingLoops > 0) {
            data.animations.addAll(animations(state))
            data.remainingLoops--
            return
        }


        if (data.nextOccurrenceSeconds > 0F) {
            // Is it time?
            if (data.nextOccurrenceSeconds <= state.animationSeconds) {
                data.remainingLoops = loopTimes.random()
                data.animations.addAll(animations(state))
                data.nextOccurrenceSeconds = -1F
            }
        } else {
            data.nextOccurrenceSeconds = state.animationSeconds + Random.nextFloat() * secondsBetweenOccurrences.random()
        }
    }
}