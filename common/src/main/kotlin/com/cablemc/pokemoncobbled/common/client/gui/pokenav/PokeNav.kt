package com.cablemc.pokemoncobbled.common.client.gui.pokenav

import com.cablemc.pokemoncobbled.common.api.gui.blitk
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.gui.summary.Summary
import com.cablemc.pokemoncobbled.common.client.keybind.currentKey
import com.cablemc.pokemoncobbled.common.client.keybind.keybinds.PokeNavigatorBinding
import com.cablemc.pokemoncobbled.common.util.cobbledResource
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

    private val buttons = mutableListOf<PokeNavImageButton>()
    private var currentSelectionPos: Pair<Int, Int> = Pair(0, 0)
    // ^ always start at the first entry
    private val rows = IntArray(MAX_BUTTONS_PER_COLUMN)
    private var aboutToClose = false

    override fun init() {
        buttons.clear()
        // Pokemon Button
        buttons.add(pokeNavImageButtonOf(0, 0, pokemon, this::onPressPokemon, TranslatableComponent("pokemoncobbled.ui.pokemon")))

        // EXIT Button
        buttons.add(pokeNavImageButtonOf(1, 0, exit, this::onPressExit, TranslatableComponent("pokemoncobbled.ui.exit")))

        buttons.forEach { button ->
            rows[button.posY]++ // To know how many buttons are in one row
            addRenderableWidget(button)
        }

        super.init()
    }

    override fun keyPressed(pKeyCode: Int, pScanCode: Int, pModifiers: Int): Boolean {
        if (pKeyCode == InputConstants.KEY_RIGHT || pKeyCode == InputConstants.KEY_D) {
            if (currentSelectionPos.first == rows[currentSelectionPos.second] - 1) { // If the current position is the last button in the row
                changeSelectionToPos(0, currentSelectionPos.second) // Reset to first button of row
            } else {
                changeSelectionToPos(currentSelectionPos.first + 1, currentSelectionPos.second) // Go one Button to the right
            }
        }
        if (pKeyCode == InputConstants.KEY_LEFT || pKeyCode == InputConstants.KEY_A) {
            if (currentSelectionPos.first == 0) { // If the current button is the first one in the row
                changeSelectionToPos(rows[currentSelectionPos.second] - 1, currentSelectionPos.second) // Go to the last Button in the row
            } else {
                changeSelectionToPos(currentSelectionPos.first - 1, currentSelectionPos.second) // Go one Button to the right
            }
        }
        if (pKeyCode == InputConstants.KEY_UP || pKeyCode == InputConstants.KEY_W) {
            if (currentSelectionPos.second == 0) { // If the current Button is in the uppermost row
                val x = currentSelectionPos.first
                val y = rows.indexOfLast { it != 0 } // Can have entries with 0 (0 representing that there is now button in that column) so the last filled one
                if (x > rows[y] - 1) { // If the row above (can also be the last due to going up in the first row puts you in the last row) has fewer buttons than the current one also go back one button
                    changeSelectionToPos(x - 1, y)
                } else {
                    changeSelectionToPos(x, y)
                }
            } else {
                changeSelectionToPos(currentSelectionPos.first, currentSelectionPos.second - 1)
            }
        }
        if (pKeyCode == InputConstants.KEY_DOWN || pKeyCode == InputConstants.KEY_S) {
            if (currentSelectionPos.second == rows.indexOfLast { it != 0 }) { // If the current Button is in the last row
                changeSelectionToPos(currentSelectionPos.first, 0) // Go back to the first row
            } else {
                val x = currentSelectionPos.first
                val y = currentSelectionPos.second + 1
                if (x > rows[y] - 1) { // Same as in up but with down
                    changeSelectionToPos(x - 1, y)
                } else {
                    changeSelectionToPos(x, y)
                }
            }
        }
        if (pKeyCode == InputConstants.KEY_SPACE) {
            buttons.firstOrNull {
                it.posX == currentSelectionPos.first && it.posY == currentSelectionPos.second
            }?.onPress() // Executes the onPress action for the currently selected button
        }
        if (pKeyCode == PokeNavigatorBinding.currentKey().value || pKeyCode == InputConstants.KEY_LSHIFT || pKeyCode == InputConstants.KEY_RSHIFT) {
            aboutToClose = true // This is needed so the UI doesn't open itself afterwards again (Closing with same key as opening) -> KeyReleased
        }

        return super.keyPressed(pKeyCode, pScanCode, pModifiers)
    }

    override fun keyReleased(pKeyCode: Int, pScanCode: Int, pModifiers: Int): Boolean {
        if ((pKeyCode == PokeNavigatorBinding.currentKey().value || pKeyCode == InputConstants.KEY_LSHIFT || pKeyCode == InputConstants.KEY_RSHIFT) && aboutToClose) {
            Minecraft.getInstance().setScreen(null) // So we only close if the Key was released
        }
        return super.keyReleased(pKeyCode, pScanCode, pModifiers)
    }

    /**
     * Change the currentSelection thingy to the position of another Button
     */
    private fun changeSelectionToPos(posX: Int, posY: Int) {
        currentSelectionPos = Pair(posX, posY)
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
    private fun pokeNavImageButtonOf(
        posX: Int,
        posY: Int,
        resourceLocation: ResourceLocation,
        onPress: Button.OnPress,
        component: Component
    ): PokeNavImageButton {
        return PokeNavImageButton(
            posX, posY,
            getWidthForPos(posX), getHeightFor(posY),
            buttonWidth, buttonHeight,
            0, 0, 0,
            resourceLocation, buttonWidth, buttonHeight,
            onPress, component
        )
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

        // Rendering Selection
        blitk(
            poseStack = pMatrixStack,
            texture = select,
            x = getWidthForPos(currentSelectionPos.first) + 2.55, y = getHeightFor(currentSelectionPos.second) + 2.45,
            width = 59, height = 34.5
        )
    }

    /**
     * Whether the screen should pause the game or not
     */
    override fun isPauseScreen(): Boolean {
        return true
    }
}