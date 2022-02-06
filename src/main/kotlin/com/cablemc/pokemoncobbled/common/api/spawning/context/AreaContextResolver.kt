package com.cablemc.pokemoncobbled.common.api.spawning.context

import com.cablemc.pokemoncobbled.common.api.spawning.WorldSlice
import com.cablemc.pokemoncobbled.common.api.spawning.context.calculators.AreaSpawningContextCalculator
import com.cablemc.pokemoncobbled.common.api.spawning.context.calculators.AreaSpawningInput
import com.cablemc.pokemoncobbled.common.api.spawning.spawner.Spawner
import net.minecraft.core.BlockPos

/**
 * Interface responsible for drawing a list of spawn contexts from a slice of the world,
 * given a list of all the context calculators that should be considered in order. As soon
 * as one of the context calculators returns true for [AreaSpawningContextCalculator.fits],
 * no other context calculator will be considered.
 *
 * The default method body of this interface checks every single block in the slice
 * and composes a single context per position, at most. This is almost certainly fine,
 * but this interface exists, so you can override it if you want.
 *
 * @author Hiroku
 * @since January 31st, 2022
 */
interface AreaContextResolver {
    fun resolve(
        spawner: Spawner,
        contextCalculators: List<AreaSpawningContextCalculator<*>>,
        slice: WorldSlice
    ): List<AreaSpawningContext> {
        val pos = BlockPos.MutableBlockPos(1, 2, 3)
        val input = AreaSpawningInput(spawner, pos, slice)
        val contexts = mutableListOf<AreaSpawningContext>()

        var x = slice.baseX
        var y = slice.baseY
        var z = slice.baseZ

        while (x < slice.baseX + slice.length) {
            while (y < slice.baseY + slice.height) {
                while (z < slice.baseZ + slice.width) {
                    pos.set(x, y, z)
                    val fittedContextCalculator = contextCalculators.firstOrNull { it.fits(input) }
                    if (fittedContextCalculator != null) {
                        val context = fittedContextCalculator.calculate(input)
                        if (context != null) {
                            contexts.add(context)
                        }
                    }
                    z++
                }
                y++
            }
            x++
        }

        return contexts
    }
}