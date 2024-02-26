package com.cobblemon.mod.common.client.gui.pokedex.widgets

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.gui.drawPortraitPokemon
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.client.gui.PartyOverlay
import com.cobblemon.mod.common.client.gui.summary.widgets.ModelWidget
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import com.cobblemon.mod.common.pokemon.Species
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.lang
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.LiteralTextContent
import net.minecraft.text.MutableText
import net.minecraft.text.OrderedText
import net.minecraft.text.Text
import net.minecraft.text.TextContent

class PokemonScrollSlot(val pX: Int, val pY: Int, val pWidth: Int, val pHeight: Int
): SoundlessWidget(pX, pY, pWidth, pHeight, Text.literal("PokemonScrollSlot")) {

    var pokemon : RenderablePokemon? = null
    var pokemonName: MutableText = lang("default")
    var pokemonNumber: MutableText = "0".text()
    var pokemonSpecies: Species? = null



    override fun renderButton(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        if(pokemon == null || pokemonSpecies == null) return

        val matrices = context.matrices

        blitk(
            matrixStack = matrices,
            texture = scrollSlotResource,
            x = pX,
            y = pY,
            width = pWidth,
            height = pHeight
        )

        drawScaledText(
            context = context,
            text = pokemonName,
            x = pX + pHeight,
            y = pY + 2
        )

        drawScaledText(
            context = context,
            text = pokemonNumber,
            x = pX + pHeight,
            y = pY + pHeight/2
        )

        context.enableScissor(
            pX,
            pY,
            pX + pHeight,
            pY + pHeight
        )

        matrices.push()
        matrices.translate(
            pX.toDouble() + pHeight.toDouble()/2,
            pY.toDouble() + pHeight.toDouble()/2 - 25,
            0.0
        )

        drawPortraitPokemon(pokemonSpecies!!, mutableSetOf(), matrices, partialTicks = 0F, scale=9F)
        matrices.pop()
        context.disableScissor()
    }

    companion object {
        private val scrollSlotResource = cobblemonResource("textures/gui/pokedex/scroll_slot_base.png")// Render Scroll Slot Background
    }

    fun setPokemon(species: Species, aspects: Set<String> = HashSet<String>()){
        pokemon = RenderablePokemon(species, aspects)
        pokemonSpecies = species
        pokemonName = species.translatedName
        pokemonNumber = "${species.nationalPokedexNumber}".text()
    }

    fun removePokemon(){
        pokemon = null
    }
}
