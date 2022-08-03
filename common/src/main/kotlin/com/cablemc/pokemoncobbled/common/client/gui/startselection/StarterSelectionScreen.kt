package com.cablemc.pokemoncobbled.common.client.gui.startselection

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.api.gui.ColourLibrary
import com.cablemc.pokemoncobbled.common.api.gui.MultiLineLabelK
import com.cablemc.pokemoncobbled.common.api.gui.blitk
import com.cablemc.pokemoncobbled.common.api.text.bold
import com.cablemc.pokemoncobbled.common.client.CobbledResources
import com.cablemc.pokemoncobbled.common.client.gui.startselection.widgets.CategoryList
import com.cablemc.pokemoncobbled.common.client.gui.startselection.widgets.ExitButton
import com.cablemc.pokemoncobbled.common.client.gui.startselection.widgets.preview.ArrowButton
import com.cablemc.pokemoncobbled.common.client.gui.startselection.widgets.preview.SelectionButton
import com.cablemc.pokemoncobbled.common.client.gui.startselection.widgets.preview.StarterRoundabout
import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.ModelWidget
import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.type.DualTypeWidget
import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.type.SingleTypeWidget
import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.type.TypeWidget
import com.cablemc.pokemoncobbled.common.client.render.drawScaledText
import com.cablemc.pokemoncobbled.common.config.starter.RenderableStarterCategory
import com.cablemc.pokemoncobbled.common.net.messages.server.SelectStarterPacket
import com.cablemc.pokemoncobbled.common.pokemon.RenderablePokemon
import com.cablemc.pokemoncobbled.common.util.asTranslated
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.cablemc.pokemoncobbled.common.util.lang
import com.cablemc.pokemoncobbled.common.util.math.toRGB
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
            println("Empty category list while opening StarterSelectionUI")
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
            CobbledNetwork.sendToServer(
                SelectStarterPacket(
                    categoryName = currentCategory.name,
                    selected = currentSelection
                )
            )
            MinecraftClient.getInstance().setScreen(null)
        }

        addDrawableChild(selectionButton)

        starterRoundaboutCenter = StarterRoundabout(
            pX = x + 119, pY = height / 2 + 86,
            pWidth = StarterRoundabout.MODEL_WIDTH, pHeight = StarterRoundabout.MODEL_HEIGHT,
            pokemon = currentPokemon
        )

        starterRoundaboutLeft = StarterRoundabout(
            pX = x + 89, pY = height / 2 + 86,
            pWidth = StarterRoundabout.MODEL_WIDTH, pHeight = StarterRoundabout.MODEL_HEIGHT,
            pokemon = currentCategory.pokemon[leftOfCurrentSelection()]
        )

        starterRoundaboutRight = StarterRoundabout(
            pX = x + 149, pY = height / 2 + 86,
            pWidth = StarterRoundabout.MODEL_WIDTH, pHeight = StarterRoundabout.MODEL_HEIGHT,
            pokemon = currentCategory.pokemon[rightOfCurrentSelection()]
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
        val (r, g, b) = currentPokemon.form.primaryType.hue.toRGB()
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
            matrixStack = matrices,
            font = CobbledResources.DEFAULT_LARGE,
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
            matrixStack = matrices,
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

        MultiLineLabelK.create(
            component = currentPokemon.species.description,
            width = 127,
            maxLines = 4
        ).renderLeftAligned(
            poseStack = matrices,
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
        typeWidget.render(matrices, mouseX, mouseY, delta)
        // Render the rest
        super.render(matrices, mouseX, mouseY, delta)
    }

    fun changeCategory(category: RenderableStarterCategory) {
        currentCategory = category
        currentSelection = 0
        updateSelection()
    }

    private fun right() {
        currentSelection = rightOfCurrentSelection()
        updateSelection()
    }

    private fun rightOfCurrentSelection() : Int = if (currentSelection + 1 <= currentCategory.pokemon.size - 1) currentSelection + 1 else 0

    private fun left() {
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