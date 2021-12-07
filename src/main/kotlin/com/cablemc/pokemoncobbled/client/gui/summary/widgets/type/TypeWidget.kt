package com.cablemc.pokemoncobbled.client.gui.summary.widgets.type

import com.cablemc.pokemoncobbled.client.gui.summary.mock.PokemonTypes
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.Widget
import net.minecraft.network.chat.Component

abstract class TypeWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    pMessage: Component
): Widget, AbstractWidget(pX, pY, pWidth, pHeight, pMessage) {

    fun of(pokemon: Pokemon,
           pX: Int, pY: Int,
           pWidth: Int, pHeight: Int,
           pMessage: Component
    ): TypeWidget {
        return DualTypeWidget(pX, pY, pWidth, pHeight, pMessage, PokemonTypes.FIRE, PokemonTypes.FLYING)
    }

}