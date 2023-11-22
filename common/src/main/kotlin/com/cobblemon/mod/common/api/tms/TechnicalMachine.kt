package com.cobblemon.mod.common.api.tms

import net.minecraft.util.Identifier

data class TechnicalMachine(
    val moveName: String,
    val recipe: TechnicalMachineRecipe,
    val obtainMethods: List<ObtainMethod>
)
