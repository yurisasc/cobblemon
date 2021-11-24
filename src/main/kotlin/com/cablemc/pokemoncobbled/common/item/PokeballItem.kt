package com.cablemc.pokemoncobbled.common.item

import com.cablemc.pokemoncobbled.common.pokemon.pokeball.Pokeball
import net.minecraft.world.item.CreativeModeTab
import net.minecraftforge.client.RenderProperties

class PokeballItem(
    pokeball : Pokeball
) : CobbledItem(Properties().tab(CreativeModeTab.TAB_TOOLS)) {

}