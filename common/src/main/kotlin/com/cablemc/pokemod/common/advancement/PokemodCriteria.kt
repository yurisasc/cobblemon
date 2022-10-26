package com.cablemc.pokemod.common.advancement

import com.cablemc.pokemod.common.advancement.criterion.PickStarterCriterion
import net.minecraft.advancement.criterion.Criteria

/**
 * Contains all the advancement criteria in Pokemod.
 *
 * @author Licious
 * @since October 26th, 2022
 */
object PokemodCriteria {

    val PICK_STARTER = Criteria.register(PickStarterCriterion())

}