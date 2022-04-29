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
    private val rows = IntArray(MAX_BUTTONS_PER_COLUMN)
    private var aboutToClose = false

    override fun init() {
        buttons.clear()
        // Pokemon Button
        this.insertButton(0, 0, pokemon, this::onPressPokemon, TranslatableComponent("pokemoncobbled.ui.pokemon")) { PokemonCobbledClient.storage.myParty.slots.filterNotNull().isNotEmpty() }

        // EXIT Button
        this.insertButton(1, 0, exit, this::onPressExit, TranslatableComponent("pokemoncobbled.ui.exit"))

        buttons.values().forEach { button ->
            rows[button.posY]++ // To know how many buttons are in one row
            addRenderableWidget(button)
            if (!button.canClick())
                addRenderableWidget(this.fillerButtonOf(button.posX, button.posY))
        }

        super.init()
    }

    /**
     * Changed by Licious April 29th 2022
     * Cleaned up the original code see [move] for how the selection moves around.
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
        this.move(movement.first, movement.second)
        return super.keyPressed(pKeyCode, pScanCode, pModifiers)
    }

    override fun keyReleased(pKeyCode: Int, pScanCode: Int, pModifiers: Int): Boolean {
        if ((pKeyCode == PokeNavigatorBinding.currentKey().value || pKeyCode == InputConstants.KEY_LSHIFT || pKeyCode == InputConstants.KEY_RSHIFT) && aboutToClose) {
            Minecraft.getInstance().setScreen(null) // So we only close if the Key was released
        }
        return super.keyReleased(pKeyCode, pScanCode, pModifiers)
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
    private fun move(x: Int, y: Int) {
        // No op necessary
        if (x == 0 && y == 0) {
            return
        }
        val currentX = this.currentSelectionPos.first
        val currentY = this.currentSelectionPos.second
        val newX = currentX + x
        val newY = currentY + y
        val button = this.buttons.get(newX, newY)
        // If there's no button at the new coordinates we don't move
        if (button != null) {
            this.currentSelectionPos = newX to newY
        }
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
        posX: Int,
        posY: Int,
        resourceLocation: ResourceLocation,
        onPress: Button.OnPress,
        component: Component,
        canClick: () -> Boolean = { true }
    ) {
        val button = PokeNavImageButton(
            posX, posY,
            getWidthForPos(posX), getHeightFor(posY),
            buttonWidth, buttonHeight,
            0, 0, 0,
            resourceLocation, buttonWidth, buttonHeight,
            onPress, component, canClick
        )
        this.buttons.put(posX, posY, button)
    }

    private fun fillerButtonOf(posX: Int, posY: Int) = PokeNavFillerButton(
        posX, posY,
        getWidthForPos(posX), getHeightFor(posY),
        buttonWidth, buttonHeight,
        0, 0, 0,
        buttonWidth, buttonHeight
    )

    /**
     * What should happen on Button press - START
     */

    private fun onPressPokemon(button: Button) {
        Minecraft.getInstance().setScreen(Summary(PokemonCobbledClient.storage.myParty))
    }

    private fun onPressExit(button: Button) {
        Minecraft.getInstance().setScreen(null)
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

    /**
     * Whether the screen should pause the game or not
     */
    override fun isPauseScreen(): Boolean {
        return true
    }
}