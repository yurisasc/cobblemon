package com.cobblemon.mod.common.api.tms

import net.minecraft.util.Identifier

data class TechnicalMachine(
    val moveId: Identifier,
    val recipe: TechnicalMachineRecipe,
    val obtainMethods: List<ObtainMethod>
)
