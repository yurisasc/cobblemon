package com.cobblemon.mod.common.client.gui.pokedex.widgets

import com.cobblemon.mod.common.client.gui.summary.widgets.ModelWidget
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import com.cobblemon.mod.common.pokemon.Species
import com.ibm.icu.text.Normalizer2.Mode
import com.sun.jna.platform.unix.X11.XSizeHints.Aspect
import net.minecraft.client.gui.DrawContext
import net.minecraft.data.client.Model
import net.minecraft.text.Text

class PokemonScrollSlot(val pX: Int, val pY: Int, val pWidth: Int, val pHeight: Int
): SoundlessWidget(pX, pY, pWidth, pHeight, Text.literal("PokemonScrollSlot")) {

    var modelWidget: ModelWidget? = null
    var pokemon : RenderablePokemon? = null

    override fun renderButton(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        if(pokemon == null || modelWidget == null) return

    }

    fun setPokemon(species: Species, aspects: Set<String> = HashSet<String>()){
        if(modelWidget != null) removeWidget(modelWidget!!)

        pokemon = RenderablePokemon(species, aspects)

        //This should never crash due to line above
        modelWidget = ModelWidget(pX, pY, width, height, pokemon!!)

        //This should never crash due to line above
        addWidget(modelWidget!!)
    }

    fun removePokemon(){
        modelWidget = null
        pokemon = null
    }

    override fun onClick(pMouseX: Double, pMouseY: Double) {
    }
}
