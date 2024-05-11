/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.pokedex.widgets

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.gui.drawPortraitPokemon
import com.cobblemon.mod.common.api.pokedex.SpeciesPokedexEntry
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.client.gui.drawProfilePokemon
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUI
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.HEIGHT_Y_POSITION
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.MARGIN
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.POKEMON_PORTRAIT_HEIGHT
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.POKEMON_PORTRAIT_WIDTH
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants.WEIGHT_Y_POSITION
import com.cobblemon.mod.common.client.gui.summary.Summary
import com.cobblemon.mod.common.client.gui.summary.widgets.ModelWidget
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.cobblemon.mod.common.client.gui.summary.widgets.type.SingleTypeWidget
import com.cobblemon.mod.common.client.gui.summary.widgets.type.TypeWidget
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonFloatingState
import com.cobblemon.mod.common.pokedex.DexPokemonData
import com.cobblemon.mod.common.pokemon.FormData
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import com.cobblemon.mod.common.pokemon.Species
import com.cobblemon.mod.common.pokemon.aspects.SHINY_ASPECT
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.lang
import com.cobblemon.mod.common.util.math.fromEulerXYZDegrees
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import org.joml.Quaternionf
import org.joml.Vector3f
import java.text.Normalizer.Form

class PokemonInfoWidget(val pX: Int, val pY: Int) : SoundlessWidget(
    pX,
    pY,
    POKEMON_PORTRAIT_WIDTH,
    POKEMON_PORTRAIT_HEIGHT,
    lang("ui.pokedex.pokemon_info"),
) {
    var dexPokemonData : DexPokemonData? = null
    var speciesPokedexEntry : SpeciesPokedexEntry? = null
    var selectedForm : String? = null
    var shiny = false
    var renderablePokemon : RenderablePokemon? = null

    var state = PokemonFloatingState()
    var rotationY = 30F
    var rotationVector = Vector3f(13F, rotationY, 0F)

    companion object {
        val scaleAmount = 3F

        val iconShinyResource = cobblemonResource("textures/gui/pokedex/icon_shiny.png")
    }

    override fun renderButton(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        if (dexPokemonData == null || renderablePokemon == null) return

        val hasKnowledge = speciesPokedexEntry != null
        val species = dexPokemonData!!.species

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
            pY.toDouble() + POKEMON_PORTRAIT_HEIGHT.toDouble()/2 - 60F,
            0.0
        )
        matrices.scale(scaleAmount, scaleAmount, scaleAmount)

        if (hasKnowledge){
            drawProfilePokemon(
                renderablePokemon =  renderablePokemon!!,
                matrixStack =  matrices,
                partialTicks = delta,
                rotation = Quaternionf().fromEulerXYZDegrees(rotationVector),
                state = state,
                r = 1F,
                g = 1F,
                b = 1F
            )
        } else {
            drawProfilePokemon(
                renderablePokemon =  renderablePokemon!!,
                matrixStack =  matrices,
                partialTicks = delta,
                rotation = Quaternionf().fromEulerXYZDegrees(rotationVector),
                state = state,
                r = 0F,
                g = 0F,
                b = 0F
            )
        }

        matrices.pop()
        context.disableScissor()

        //Weight
        if (hasKnowledge) {
            drawScaledText(
                context = context,
                text = "${species?.weight}${Text.translatable("cobblemon.ui.pokedex.weightunit").string}".text(),
                x = pX + MARGIN,
                y = pY + WEIGHT_Y_POSITION,
                shadow = true
            )
        } else {
            drawScaledText(
                context = context,
                text = Text.translatable("cobblemon.ui.pokedex.unknown"),
                x = pX + MARGIN,
                y = pY + WEIGHT_Y_POSITION,
                shadow = true
            )
        }

        //Height
        if (hasKnowledge) {
            drawScaledText(
                context = context,
                text = "${species?.height}${Text.translatable("cobblemon.ui.pokedex.heightunit").string}".text(),
                x = pX + MARGIN,
                y = pY + HEIGHT_Y_POSITION,
                shadow = true
            )
        } else {
            drawScaledText(
                context = context,
                text = Text.translatable("cobblemon.ui.pokedex.unknown"),
                x = pX + MARGIN,
                y = pY + HEIGHT_Y_POSITION,
                shadow = true
            )
        }
    }

    fun setPokemon(dexPokemonData : DexPokemonData, speciesPokedexEntry: SpeciesPokedexEntry?){
        this.dexPokemonData = dexPokemonData
        if(dexPokemonData.forms.size > 0){
            selectedForm = dexPokemonData.forms.first()
        } else {
            selectedForm = "normal"
        }
        changedAspects()
    }

    fun setForm(form: String){
        if (selectedForm != form){
            selectedForm = form
            changedAspects()
        }
    }

    fun changedAspects(){
        if(dexPokemonData == null) return

        val aspects = mutableSetOf<String>()
        aspects.add(selectedForm!!)
        if(shiny){
            aspects.add(SHINY_ASPECT.aspect)
        }

        renderablePokemon = dexPokemonData!!.species?.let { RenderablePokemon(it, aspects) }
    }
}