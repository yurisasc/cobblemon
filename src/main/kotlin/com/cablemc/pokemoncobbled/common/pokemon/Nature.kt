package com.cablemc.pokemoncobbled.common.pokemon

import com.cablemc.pokemoncobbled.common.api.item.Flavor
import com.cablemc.pokemoncobbled.common.pokemon.stats.Stat
import net.minecraft.resources.ResourceLocation

class Nature(
    val name: ResourceLocation,
    val increasedStat: Stat?,
    val decreasedStat: Stat?,
    val favoriteFlavor: Flavor?,
    val dislikedFlavor: Flavor?
) {

}