/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.startselection

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.CobblemonNetwork
import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.gui.ColourLibrary
import com.cobblemon.mod.common.api.gui.MultiLineLabelK
import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.client.CobblemonResources
import com.cobblemon.mod.common.client.gui.startselection.widgets.CategoryList
import com.cobblemon.mod.common.client.gui.startselection.widgets.ExitButton
import com.cobblemon.mod.common.client.gui.startselection.widgets.preview.ArrowButton
import com.cobblemon.mod.common.client.gui.startselection.widgets.preview.SelectionButton
import com.cobblemon.mod.common.client.gui.startselection.widgets.preview.StarterRoundabout
import com.cobblemon.mod.common.client.gui.summary.widgets.ModelWidget
import com.cobblemon.mod.common.client.gui.summary.widgets.type.DualTypeWidget
import com.cobblemon.mod.common.client.gui.summary.widgets.type.SingleTypeWidget
import com.cobblemon.mod.common.client.gui.summary.widgets.type.TypeWidget
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.config.starter.RenderableStarterCategory
import com.cobblemon.mod.common.net.messages.server.SelectStarterPacket
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import com.cobblemon.mod.common.util.asTranslated
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.lang
import com.cobblemon.mod.common.util.math.toRGB
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.text.Text

/**
 * Starterselection Screen Thingy
 *
 * @author Qu
 * @since 2022-06-18
 */
class StarterSelectionScreen private constructor(): Screen("cobblemon.ui.starter.title".asTranslated()) {

    companion object {
        // Size of UI at scale 1
        private const val BASE_WIDTH = 200
        private const val BASE_HEIGHT = 175

        // Resources
        private val base = cobblemonResource("textures/gui/starterselection/starterselection_base.png")
        private val baseUnderlay = cobblemonResource("textures/gui/starterselection/starterselection_base_underlay.png")
        private val baseFrame = cobblemonResource("textures/gui/starterselection/starterselection_base_frame.png")

        // Type Backgrounds
        private val singleTypeBackground = cobblemonResource("textures/gui/starterselection/starterselection_type_slot1.png")
        private val doubleTypeBackground = cobblemonResource("textures/gui/starterselection/starterselection_type_slot2.png")
    }

    lateinit var categories: List<RenderableStarterCategory>
    lateinit var currentCategory: RenderableStarterCategory
    lateinit var modelWidget: ModelWidget
    lateinit var currentPokemon: RenderablePokemon
    var currentSelection = 0
    lateinit var rightButton: ArrowButton
    lateinit var leftButton: ArrowButton
    lateinit var typeWidget: TypeWidget
    lateinit var selectionButton: SelectionButton
    lateinit var starterRoundaboutCenter: StarterRoundabout
    lateinit var starterRoundaboutLeft: StarterRoundabout
    lateinit var starterRoundaboutRight: StarterRoundabout

    constructor(categories: List<RenderableStarterCategory>) : this() {
        this.categories = categories
    }

    override fun init() {
        super.init()

        val x = (width - BASE_WIDTH) / 2
        val y = (height - BASE_HEIGHT) / 2

        if (categories.isEmpty()) {
            Cobblemon.LOGGER.warn("Empty category list while opening StarterSelectionUI")
            return
        }

        addDrawableChild(
            CategoryList(
                paneWidth = 71, paneHeight = BASE_HEIGHT - 11,
                topOffset = 6, bottomOffset = 5,
                entryHeight = 20, entryWidth = 57,
                categories = categories,
                x = x - 2, y = y + 8,
                starterSelectionScreen = this
            )
        )

        rightButton = ArrowButton(
            pX = x + 183, pY = y + 151,
            pWidth = 9, pHeight = 14,
            right = true
        ) {
            right()
        }

        leftButton = ArrowButton(
            pX = x + 72, pY = y + 151,
            pWidth = 9, pHeight = 14,
            right = false
        ) {
            left()
        }

        addDrawableChild(rightButton)
        addDrawableChild(leftButton)

        currentCategory = categories.first()
        currentPokemon = currentCategory.pokemon[currentSelection]

        with(currentPokemon) {
            modelWidget = ModelWidget(
                pX = x + 85, pY = y + 50,
                pWidth = 102, pHeight = 100,
                pokemon = this,
                baseScale = 2.0f
            )

            typeWidget = typeWidget(this, x, y)
        }

        addDrawableChild(modelWidget)

        selectionButton = SelectionButton(
            pX = x + 106, pY = y + 124,
            pWidth = SelectionButton.BUTTON_WIDTH, pHeight = SelectionButton.BUTTON_HEIGHT
        ) {
            CobblemonNetwork.sendPacketToServer(
                SelectStarterPacket(
                    categoryName = currentCategory.name,
                    selected = currentSelection
                )
            )
            MinecraftClient.getInstance().setScreen(null)
        }

        addDrawableChild(selectionButton)

        starterRoundaboutCenter = StarterRoundabout(
            pX = x + 119, pY = height / 2 + 84,
            pWidth = StarterRoundabout.MODEL_WIDTH, pHeight = StarterRoundabout.MODEL_HEIGHT,
            pokemon = currentPokemon,
            rotationVector = this.modelWidget.rotVec
        )

        starterRoundaboutLeft = StarterRoundabout(
            pX = x + 89, pY = height / 2 + 84,
            pWidth = StarterRoundabout.MODEL_WIDTH, pHeight = StarterRoundabout.MODEL_HEIGHT,
            pokemon = currentCategory.pokemon[leftOfCurrentSelection()],
            clickAction = { _, _ -> this.left()  },
            rotationVector = this.modelWidget.rotVec
        )

        starterRoundaboutRight = StarterRoundabout(
            pX = x + 149, pY = height / 2 + 84,
            pWidth = StarterRoundabout.MODEL_WIDTH, pHeight = StarterRoundabout.MODEL_HEIGHT,
            pokemon = currentCategory.pokemon[rightOfCurrentSelection()],
            clickAction = { _, _ -> this.right()  },
            rotationVector = this.modelWidget.rotVec
        )

        addDrawableChild(starterRoundaboutLeft)
        addDrawableChild(starterRoundaboutCenter)
        addDrawableChild(starterRoundaboutRight)

        addDrawableChild(
            ExitButton(
                pX = x + 181, pY = y + 2,
                pWidth = 16, pHeight = 12,
                pXTexStart = 0, pYTexStart = 0, pYDiffText = 0
            ) {
                MinecraftClient.getInstance().setScreen(null)
                MinecraftClient.getInstance().soundManager.play(PositionedSoundInstance.master(CobblemonSounds.GUI_CLICK, 1.0F))
            }
        )
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val matrices = context.matrices
        val x = (width - BASE_WIDTH) / 2
        val y = (height - BASE_HEIGHT) / 2
        // Render Underlay
        blitk(
            matrixStack = matrices,
            texture = baseUnderlay,
            x = x, y = y,
            width = BASE_WIDTH, height = BASE_HEIGHT
        )
        // Render Base
        blitk(
            matrixStack = matrices,
            texture = base,
            x = x, y = y,
            width = BASE_WIDTH, height = BASE_HEIGHT
        )
        // Render Frame
        val (r, g, b) = currentPokemon.form.primaryType.clientData.hue.toRGB()
        blitk(
            matrixStack = matrices,
            texture = baseFrame,
            red = r,
            green = g,
            blue = b,
            x = x, y = y,
            width = BASE_WIDTH, height = BASE_HEIGHT
        )
        // Render Text
        drawScaledText(
            context = context,
            font = CobblemonResources.DEFAULT_LARGE,
            text = lang("ui.starter.title").bold(),
            x = x + 125, y = y + 3F,
            centered = true,
            scale = 1.4F,
            maxCharacterWidth = 120,
            shadow = true
        )

        // Render Name
        val pokemonName = currentPokemon.species.translatedName
        val scale = 0.8F
        drawScaledText(
            context = context,
            text = pokemonName,
            centered = true,
            scale = scale,
            maxCharacterWidth = 50,
            x = x + 94,
            y = y + 19.5,
            shadow = false
        )

        // Render Description
        val scale2 = 0.60F
        matrices.push()
        matrices.scale(scale2, scale2, 1F)

        // TODO use all pokedex lines across multiple clickable pages in this screen
        MultiLineLabelK.create(
            component = currentPokemon.form.pokedex.first().asTranslated(),
            width = 127,
            maxLines = 4
        ).renderLeftAligned(
            context = context,
            x = (x + 119) / scale2 + 4, y = (y + 18) / scale2 + 4.0,
            ySpacing = (8.0 / scale2) - 1.25,
            colour = ColourLibrary.WHITE, shadow = false
        )
        matrices.pop()

        // Render the type background
        blitk(
            matrixStack = matrices,
            texture = currentPokemon.form.secondaryType?.let { doubleTypeBackground } ?: singleTypeBackground,
            x = (currentPokemon.form.secondaryType?.let { x + 76.75 } ?: (x + 85.25)), y = y + 29.4,
            width = currentPokemon.form.secondaryType?.let { 35.25 } ?: 19, height = 19.25
        )
        // Render the type widget
        typeWidget.render(context, mouseX, mouseY, delta)
        // Render the rest
        super.render(context, mouseX, mouseY, delta)
    }

    fun changeCategory(category: RenderableStarterCategory) {
        currentCategory = category
        currentSelection = 0
        updateSelection()
    }

    private fun right() {
        MinecraftClient.getInstance().soundManager.play(PositionedSoundInstance.master(CobblemonSounds.GUI_CLICK, 1.0F))
        currentSelection = rightOfCurrentSelection()
        updateSelection()
    }

    private fun rightOfCurrentSelection() : Int = if (currentSelection + 1 <= currentCategory.pokemon.size - 1) currentSelection + 1 else 0

    private fun left() {
        MinecraftClient.getInstance().soundManager.play(PositionedSoundInstance.master(CobblemonSounds.GUI_CLICK, 1.0F))
        currentSelection = leftOfCurrentSelection()
        updateSelection()
    }

    private fun leftOfCurrentSelection() : Int = if (currentSelection - 1 == -1) currentCategory.pokemon.size - 1 else currentSelection - 1

    private fun updateSelection() {
        currentPokemon = currentCategory.pokemon[currentSelection].also {
            modelWidget.pokemon = it
            typeWidget = typeWidget(it, (width - BASE_WIDTH) / 2, (height - BASE_HEIGHT) / 2)
        }
        starterRoundaboutLeft.pokemon = currentCategory.pokemon[leftOfCurrentSelection()]
        starterRoundaboutCenter.pokemon = currentPokemon
        starterRoundaboutRight.pokemon = currentCategory.pokemon[rightOfCurrentSelection()]
    }

    private fun typeWidget(pokemon: RenderablePokemon, x: Int, y: Int) : TypeWidget {
        return pokemon.form.secondaryType?.let {
            DualTypeWidget(
                pX = x + 77, pY = y + 30,
                pWidth = 18, pHeight = 18,
                pMessage = Text.of("What?"),
                mainType = pokemon.form.primaryType, secondaryType = it
            )
        } ?: SingleTypeWidget(
            pX = x + 85, pY = y + 30,
            pWidth = 18, pHeight = 18,
            type = pokemon.form.primaryType,
            renderText = false
        )
    }

    override fun shouldPause() = true
}