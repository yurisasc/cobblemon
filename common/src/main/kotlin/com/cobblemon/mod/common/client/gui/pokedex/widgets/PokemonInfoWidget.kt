package com.cobblemon.mod.common.client.gui.pokedex.widgets

import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUI
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.cobblemon.mod.common.util.lang
import net.minecraft.client.gui.DrawContext

class PokemonInfoWidget(pX: Int, pY: Int) : SoundlessWidget(
    pX,
    pY,
    PokedexGUI.POKEMON_PORTRAIT_WIDTH,
    PokedexGUI.POKEMON_PORTRAIT_HEIGHT,
    lang("ui.pokedex.pokemon_info"),
) {
    override fun renderButton(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        TODO("Not yet implemented")
    }


}