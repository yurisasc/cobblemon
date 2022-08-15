package com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements

import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements.template.EntityQueryRequirement
import net.minecraft.entity.LivingEntity
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

/**
 * A [EntityQueryRequirement] for when a [Pokemon] is expected to be in a certain area.
 *
 * @property box The [Box] expected to be in.
 * @author Licious
 * @since March 21st, 2022
 */
class AreaRequirement : EntityQueryRequirement {
    companion object {
        const val ADAPTER_VARIANT = "area"
    }

    val box: Box = Box.from(Vec3d.ZERO)
    override fun check(pokemon: Pokemon, queriedEntity: LivingEntity) = box.contains(queriedEntity.pos)
}