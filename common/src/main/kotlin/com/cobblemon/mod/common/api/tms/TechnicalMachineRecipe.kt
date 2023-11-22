package com.cobblemon.mod.common.api.tms

import com.google.gson.annotations.SerializedName
import net.minecraft.util.Identifier

/**
 * Represents the ingredients necessary to craft a TM in the TM Machine
 * Maps an item to the number required of the item
 */
data class TechnicalMachineRecipe(
    @SerializedName("ingredients")
    val ingredientMap: MutableMap<Identifier, Int>
)