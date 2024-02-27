package com.cobblemon.mod.common.client.gui.pokedex.widgets

import com.cobblemon.mod.common.api.gui.drawPortraitPokemon
import com.cobblemon.mod.common.api.pokedex.SpeciesPokedexEntry
import com.cobblemon.mod.common.client.gui.drawProfilePokemon
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUI
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.POKEMON_PORTRAIT_HEIGHT
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.POKEMON_PORTRAIT_WIDTH
import com.cobblemon.mod.common.client.gui.summary.widgets.ModelWidget
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonFloatingState
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import com.cobblemon.mod.common.pokemon.Species
import com.cobblemon.mod.common.util.lang
import com.cobblemon.mod.common.util.math.fromEulerXYZDegrees
import net.minecraft.client.gui.DrawContext
import org.joml.Quaternionf
import org.joml.Vector3f

class PokemonInfoWidget(val pX: Int, val pY: Int) : SoundlessWidget(
    pX,
    pY,
    POKEMON_PORTRAIT_WIDTH,
    POKEMON_PORTRAIT_HEIGHT,
    lang("ui.pokedex.pokemon_info"),
) {
    var pokemonEntry : Pair<Species, SpeciesPokedexEntry?>? = null
    var renderablePokemon : RenderablePokemon? = null
    var aspects : Set<String> = mutableSetOf()
    var state = PokemonFloatingState()
    var shiny = false
    var rotationY = 10F
    var rotationVector = Vector3f(13F, rotationY, 0F)

    companion object {
        val scaleAmount = 3F
    }

    override fun renderButton(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        if (pokemonEntry == null || renderablePokemon == null) return

        val matrices = context.matrices

        context.enableScissor(
            pX,
            pY,
            pX + POKEMON_PORTRAIT_WIDTH,
            pY + POKEMON_PORTRAIT_HEIGHT
        )

        matrices.push()
        matrices.translate(
            pX.toDouble() + POKEMON_PORTRAIT_WIDTH.toDouble()/2,
            pY.toDouble() + POKEMON_PORTRAIT_HEIGHT.toDouble()/2 - 50F,
            0.0
        )
        matrices.scale(scaleAmount, scaleAmount, scaleAmount)

        drawProfilePokemon(
            renderablePokemon =  renderablePokemon!!,
            matrixStack =  matrices,
            partialTicks = delta,
            rotation = Quaternionf().fromEulerXYZDegrees(rotationVector),
            state = state,
        )

        matrices.pop()
        context.disableScissor()
    }

    fun setPokemon(pokemon : Pair<Species, SpeciesPokedexEntry?>){
        pokemonEntry = pokemon
        renderablePokemon = RenderablePokemon(species = pokemon.first, aspects)
    }
}