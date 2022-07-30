package com.cablemc.pokemoncobbled.common.client.gui.startselection

import com.cablemc.pokemoncobbled.common.api.gui.ColourLibrary
import com.cablemc.pokemoncobbled.common.api.gui.MultiLineLabelK
import com.cablemc.pokemoncobbled.common.api.gui.blitk
import com.cablemc.pokemoncobbled.common.config.starter.StarterCategory
import com.cablemc.pokemoncobbled.common.util.asTranslated
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.cablemc.pokemoncobbled.common.api.gui.drawCenteredText
import com.cablemc.pokemoncobbled.common.api.gui.drawText
import com.cablemc.pokemoncobbled.common.client.CobbledResources
import com.cablemc.pokemoncobbled.common.client.gui.startselection.widgets.CategoryList
import com.cablemc.pokemoncobbled.common.client.gui.startselection.widgets.ExitButton
import com.cablemc.pokemoncobbled.common.client.gui.startselection.widgets.preview.ArrowButton
import com.cablemc.pokemoncobbled.common.client.gui.startselection.widgets.preview.SelectionButton
import com.cablemc.pokemoncobbled.common.client.gui.startselection.widgets.preview.StarterRoundabout
import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.ModelWidget
import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.pages.moves.MoveInfoWidget
import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.type.DualTypeWidget
import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.type.SingleTypeWidget
import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.type.TypeWidget
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text

/**
 * Starterselection Screen Thingy
 *
 * @author Qu
 * @since 2022-06-18
 */
class StarterSelectionScreen private constructor(): Screen("pokemoncobbled.ui.starter.title".asTranslated()) {

    companion object {
        // Size of UI at scale 1
        private const val BASE_WIDTH = 200
        private const val BASE_HEIGHT = 175

        // Resources
        private val base = cobbledResource("ui/starterselection/starterselection_base.png")
        private val baseUnderlay = cobbledResource("ui/starterselection/starterselection_base_underlay.png")
        private val baseFrame = cobbledResource("ui/starterselection/starterselection_base_frame.png")

        // Type Backgrounds
        private val singleTypeBackground = cobbledResource("ui/starterselection/starterselection_type_slot1.png")
        private val doubleTypeBackground = cobbledResource("ui/starterselection/starterselection_type_slot2.png")
    }

    lateinit var categories: List<StarterCategory>
    lateinit var currentCategory: StarterCategory
    lateinit var modelWidget: ModelWidget
    lateinit var currentPokemon: Pokemon
    var currentSelection = 0
    lateinit var rightButton: ArrowButton
    lateinit var leftButton: ArrowButton
    lateinit var typeWidget: TypeWidget
    lateinit var selectionButton: SelectionButton
    lateinit var starterRoundabout: StarterRoundabout

    constructor(categories: List<StarterCategory>) : this() {
        this.categories = categories
    }

    override fun init() {
        super.init()

        val x = (width - BASE_WIDTH) / 2
        val y = (height - BASE_HEIGHT) / 2

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
        currentPokemon = currentCategory.pokemon[currentSelection].create()

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
            println("Selected ${currentPokemon.species.name}")
        }

        addDrawableChild(selectionButton)

        starterRoundabout = StarterRoundabout(
            pX = x + 120, pY = 140,
            pWidth = StarterRoundabout.MODEL_WIDTH * 3, pHeight = StarterRoundabout.MODEL_HEIGHT,
            starterSelectionScreen = this
        )

        addDrawableChild(starterRoundabout)

        addDrawableChild(
            ExitButton(
                pX = x + 181, pY = y + 2,
                pWidth = 16, pHeight = 12,
                pXTexStart = 0, pYTexStart = 0, pYDiffText = 0
            ) {
                MinecraftClient.getInstance().setScreen(null)
            }
        )
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
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
        blitk(
            matrixStack = matrices,
            texture = baseFrame,
            x = x, y = y,
            width = BASE_WIDTH, height = BASE_HEIGHT
        )
        // Render Text
        matrices.push()
        matrices.scale(0.75f, 0.95f, 0.95f)
        drawCenteredText(
            poseStack = matrices,
            font = CobbledResources.NOTO_SANS_BOLD,
            text = "pokemoncobbled.ui.starter.title".asTranslated(),
            x = (x + 122.5) / 0.75f, y = (y + 1.5f) / 0.95f,
            colour = ColourLibrary.WHITE,
            shadow = false
        )
        matrices.pop()

        // Render Name
        val scale = 0.60F
        matrices.push()
        matrices.scale(scale, scale, 1F)
        drawText(
            poseStack = matrices, font = CobbledResources.NOTO_SANS_BOLD_SMALL,
            text = currentPokemon.species.translatedName,
            x = (x + 71) / scale + 1.5, y = (y + 18) / scale +3.25,
            colour = ColourLibrary.WHITE, shadow = false
        )
        matrices.pop()

        // Render Description
        val scale2 = 0.60F
        matrices.push()
        matrices.scale(scale2, scale2, 1F)
        MultiLineLabelK.create(
            component = "pokemoncobbled.species.${currentPokemon.species.name}.desc".asTranslated(),
            width = 127,
            maxLines = 4,
            font = CobbledResources.NOTO_SANS_REGULAR
        ).renderLeftAligned(
            poseStack = matrices,
            x = (x + 119) / scale + 3.85, y = (y + 18) / scale + 4.0,
            ySpacing = (8.0 / scale2) - 1.25,
            colour = ColourLibrary.WHITE, shadow = false
        )
        matrices.pop()

        // Render the type background
        blitk(
            matrixStack = matrices,
            texture = currentPokemon.secondaryType?.let { doubleTypeBackground } ?: singleTypeBackground,
            x = (currentPokemon.secondaryType?.let { x + 76.75 } ?: (x + 85.25)), y = y + 29.4,
            width = currentPokemon.secondaryType?.let { 35.25 } ?: 19, height = 19.25
        )
        // Render the type widget
        typeWidget.render(matrices, mouseX, mouseY, delta)
        // Render the rest
        super.render(matrices, mouseX, mouseY, delta)
    }

    fun changeCategory(category: StarterCategory) {
        currentCategory = category
        currentSelection = 0
        updateSelection()
    }

    private fun right() {
        if (currentSelection + 1 <= currentCategory.pokemon.size - 1) {
            currentSelection++
        } else {
            currentSelection = 0
        }
        updateSelection()
    }

    private fun left() {
        if (currentSelection - 1 == -1) {
            currentSelection = currentCategory.pokemon.size - 1
        } else {
            currentSelection--
        }
        updateSelection()
    }

    private fun updateSelection() {
        currentPokemon = currentCategory.pokemon[currentSelection].create().also {
            modelWidget.pokemon = it
            typeWidget = typeWidget(it, (width - BASE_WIDTH) / 2, (height - BASE_HEIGHT) / 2)
        }
    }

    private fun typeWidget(pokemon: Pokemon, x: Int, y: Int) : TypeWidget {
        return pokemon.secondaryType?.let {
            DualTypeWidget(
                pX = x + 77, pY = y + 30,
                pWidth = 18, pHeight = 18,
                pMessage = Text.of("What?"),
                mainType = pokemon.primaryType, secondaryType = it
            )
        } ?: SingleTypeWidget(
            pX = x + 85, pY = y + 30,
            pWidth = 18, pHeight = 18,
            type = pokemon.primaryType,
            renderText = false
        )
    }

    override fun shouldPause() = true
}