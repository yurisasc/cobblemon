package com.cablemc.pokemoncobbled.common.client.gui.pokenav

import com.cablemc.pokemoncobbled.common.api.gui.blitk
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.gui.summary.Summary
import com.cablemc.pokemoncobbled.common.client.keybind.currentKey
import com.cablemc.pokemoncobbled.common.client.keybind.keybinds.PokeNavigatorBinding
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table
import com.mojang.blaze3d.platform.InputConstants
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TranslatableComponent
import net.minecraft.resources.ResourceLocation

class PokeNav: Screen(TranslatableComponent("pokemoncobbled.ui.pokenav.title")) {

    companion object {
        // Limiting
        private const val MAX_BUTTONS_PER_ROW = 3
        private const val MAX_BUTTONS_PER_COLUMN = 2
        // Spacing between Button and Border / other Buttons
        private const val HORIZONTAL_SPACING = 8
        private const val VERTICAL_SPACING = 26
        // Size of Background
        private const val backgroundHeight = 125
        private const val backgroundWidth = 218
        // Size of Buttons
        private const val buttonHeight = 39
        private const val buttonWidth = 64
        // Textures
        private val background = cobbledResource("ui/pokenav/pokenav_base.png")
        private val exit = cobbledResource("ui/pokenav/pokenav_exit.png")
        private val pokemon = cobbledResource("ui/pokenav/pokenav_pokemon.png")
        private val select = cobbledResource("ui/pokenav/pokenav_select.png")
    }

    /**
     * Changed by Licious April 29th 2022
     * Seemed to make more sense for our use case.
     */
    private val buttons: Table<Int, Int, PokeNavImageButton> = HashBasedTable.create()
    private var currentSelectionPos = 0 to 0
    // ^ always start at the first entry
    private var aboutToClose = false
    // Start waiting for key movements until the mouse is moved for the first time
    private var focusWithKey = true

    override fun init() {
        this.buttons.clear()
        // Pokemon Button
        this.insertButton(pokemon, this::onPressPokemon, TranslatableComponent("pokemoncobbled.ui.pokemon")) { PokemonCobbledClient.storage.myParty.slots.filterNotNull().isNotEmpty() }

        // EXIT Button
        this.insertButton(exit, this::onPressExit, TranslatableComponent("pokemoncobbled.ui.exit"))

        this.buttons.values().forEach { button ->
            addRenderableWidget(button)
            if (!button.canClick())
                addRenderableWidget(this.fillerButtonOf(button.posX, button.posY))
        }

        super.init()
    }

    /**
     * Changed by Licious April 29th 2022
     * Cleaned up the original code see [moveSelected] for how the selection moves around.
     */
    override fun keyPressed(pKeyCode: Int, pScanCode: Int, pModifiers: Int): Boolean {
        val movement: Pair<Int, Int> = when (pKeyCode) {
            InputConstants.KEY_RIGHT, InputConstants.KEY_D -> 1 to 0
            InputConstants.KEY_LEFT, InputConstants.KEY_A -> -1 to 0
            InputConstants.KEY_UP, InputConstants.KEY_W -> 0 to -1
            InputConstants.KEY_DOWN, InputConstants.KEY_S -> 0 to 1
            InputConstants.KEY_SPACE -> {
                buttons.get(currentSelectionPos.first, currentSelectionPos.second)?.onPress()
                0 to 0
            }
            PokeNavigatorBinding.currentKey().value, InputConstants.KEY_LSHIFT, InputConstants.KEY_RSHIFT -> {
                this.aboutToClose = true
                0 to 0
            }
            else -> 0 to 0
        }
        this.moveSelected(movement.first, movement.second)
        return super.keyPressed(pKeyCode, pScanCode, pModifiers)
    }

    override fun keyReleased(pKeyCode: Int, pScanCode: Int, pModifiers: Int): Boolean {
        if ((pKeyCode == PokeNavigatorBinding.currentKey().value || pKeyCode == InputConstants.KEY_LSHIFT || pKeyCode == InputConstants.KEY_RSHIFT) && aboutToClose) {
            Minecraft.getInstance().setScreen(null) // So we only close if the Key was released
        }
        return super.keyReleased(pKeyCode, pScanCode, pModifiers)
    }

    /**
     * What should happen on Button press - END
     */

    /**
     * Rendering the background texture
     */
    override fun render(pMatrixStack: PoseStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        renderBackground(pMatrixStack)

        // Rendering UI Background
        blitk(
            poseStack = pMatrixStack,
            texture = background,
            x = (width - backgroundWidth) / 2, y = (height - backgroundHeight) / 2,
            width = backgroundWidth, height = backgroundHeight
        )

        super.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks)

        /**
         * Rendering Selection
         *
         * Changed by Licious April 29th 2022
         * Get selected button and gray out the selection if the button can't be clicked
         */
        val selectedButton = this.buttons.get(currentSelectionPos.first, currentSelectionPos.second) ?: return
        if (!this.focusWithKey) {
            this.buttons.values().forEach { button ->
                if (pMouseX in button.x..(button.x + button.width) && pMouseY in button.y..(button.y + button.height)) {
                    this.currentSelectionPos = button.posX to button.posY
                }
            }
        }
        blitk(
            poseStack = pMatrixStack,
            texture = select,
            x = getWidthForPos(currentSelectionPos.first) + 2.55, y = getHeightFor(currentSelectionPos.second) + 2.45,
            width = 59, height = 34.5,
            red = if (selectedButton.canClick()) 1 else PokeNavFillerButton.RED,
            green = if (selectedButton.canClick()) 1 else PokeNavFillerButton.GREEN,
            blue = if (selectedButton.canClick()) 1 else PokeNavFillerButton.BLUE,
            alpha = if (selectedButton.canClick()) 1 else PokeNavFillerButton.ALPHA,
        )
    }

    override fun afterMouseMove() {
        this.focusWithKey = false
        super.afterMouseMove()
    }

    /**
     * Whether the screen should pause the game or not
     */
    override fun isPauseScreen(): Boolean {
        return true
    }

    /**
     * Moves the selection with the given params.
     *
     * @param x How many slots to move in the X direction.
     * @param y How many slots to move in the Y direction.
     *
     * @author Licious
     * @since April 29th, 2022
     */
    private fun moveSelected(x: Int, y: Int) {
        // No op necessary
        if (x == 0 && y == 0) {
            return
        }
        this.focusWithKey = true
        val currentX = this.currentSelectionPos.first
        val currentY = this.currentSelectionPos.second
        val newX = when {
            this.buttonExists(currentX + x, currentY) -> currentX + x
            currentX + x < 0 -> this.currentMaxRow()
            else -> 0
        }
        val newY = when {
            this.buttonExists(currentX, currentY + y) -> currentY + y
            currentY + y < 0 -> this.currentMaxColumn()
            else -> 0
        }
        // If there's no button at the new coordinates we don't move
        if (this.buttonExists(newX, newY)) {
            this.currentSelectionPos = newX to newY
        }
    }

    private fun buttonExists(x: Int, y: Int) = this.buttons.get(x, y) != null

    /**
     * Finds the highest row N possible on the current column.
     *
     * @return The highest row.
     */
    private fun currentMaxRow(): Int {
        val y = this.currentSelectionPos.second
        for (x in MAX_BUTTONS_PER_ROW downTo 0) {
            if (!this.buttonExists(x, y)) {
                return x - 1
            }
        }
        return MAX_BUTTONS_PER_ROW
    }

    /**
     * Finds the highest column N possible on the current row.
     *
     * @return The highest column.
     */
    private fun currentMaxColumn(): Int {
        val x = this.currentSelectionPos.first
        for (y in MAX_BUTTONS_PER_COLUMN downTo 0) {
            if (!this.buttonExists(x, y)) {
                return y - 1
            }
        }
        return MAX_BUTTONS_PER_COLUMN
    }

    /**
     * Method for calculating the width based on the background, spacing and button position
     */
    private fun getWidthForPos(posX: Int): Int {
        return (width - backgroundWidth) / 2 + (posX + 1) * HORIZONTAL_SPACING + posX * buttonWidth - if (posX != 0) 3 else 0
    }

    /**
     * Method for calculating the height based on the background, spacing and button position
     */
    private fun getHeightFor(posY: Int): Int {
        return (height - backgroundHeight) / 2 + posY * buttonHeight + posY * VERTICAL_SPACING + if (posY == 0) 8 else 0
    }

    /**
     * To simplify creating PositionAwareImageButtons
     */
    private fun insertButton(
        resourceLocation: ResourceLocation,
        onPress: Button.OnPress,
        component: Component,
        canClick: () -> Boolean = { true }
    ) {
        val insertion = this.findNextInsertion()
        val posX = insertion.first
        val posY = insertion.second
        this.buttons.put(posX, posY, PokeNavImageButton(
            posX, posY,
            getWidthForPos(posX), getHeightFor(posY),
            buttonWidth, buttonHeight,
            0, 0, 0,
            resourceLocation, buttonWidth, buttonHeight,
            onPress, component, canClick
        ))
    }

    private fun fillerButtonOf(posX: Int, posY: Int) = PokeNavFillerButton(
        posX, posY,
        getWidthForPos(posX), getHeightFor(posY),
        buttonWidth, buttonHeight,
        0, 0, 0,
        buttonWidth, buttonHeight
    )

    private fun findNextInsertion(): Pair<Int, Int> {
        for (y in 0 until MAX_BUTTONS_PER_COLUMN) {
            for (x in 0 until MAX_BUTTONS_PER_ROW) {
                if (this.buttons.get(x, y) == null) {
                    return x to y
                }
            }
        }
        throw IllegalStateException("Cannot fit more buttons")
    }

    /**
     * What should happen on Button press - START
     */

    private fun onPressPokemon(button: Button) {
        Minecraft.getInstance().setScreen(Summary(PokemonCobbledClient.storage.myParty))
    }

    private fun onPressExit(button: Button) {
        Minecraft.getInstance().setScreen(null)
    }

}