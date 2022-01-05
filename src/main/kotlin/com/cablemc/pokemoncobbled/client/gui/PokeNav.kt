package com.cablemc.pokemoncobbled.client.gui

import com.cablemc.pokemoncobbled.client.keybinding.PokeNavigatorBinding
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.mojang.blaze3d.platform.InputConstants
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.ImageButton
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.network.chat.TranslatableComponent
import net.minecraft.resources.ResourceLocation

class PokeNav: Screen(TranslatableComponent("pokemoncobbled.ui.pokenav.title")) {

    companion object {
        // Limiting
        private const val MAX_BUTTONS_PER_ROW = 4
        private const val MAX_BUTTONS_PER_COLUMN = 3
        // Spacing between Button and Border / other Buttons
        private const val SPACING = 10
        // Size of Background
        private const val backgroundHeight = 130
        private const val backgroundWidth = 130
        // Size of Buttons
        private const val buttonHeight = 50
        private const val buttonWidth = 50
        // Textures
        private val background = cobbledResource("ui/pokenav/test.png")
        private val pokedex = cobbledResource("ui/pokenav/pokedex.png")
        private val bag = cobbledResource("ui/pokenav/bag.png")
        private val question = cobbledResource("ui/pokenav/question.png")
        private val selectionTexture = cobbledResource("ui/pokenav/selection.png")
    }

    private val buttons = mutableListOf<PositionAwareImageButton>()
    private lateinit var selection: ImageButton
    private var currentSelectionPos: Pair<Int, Int> = Pair(0, 0)
    // ^ always start at the first entry
    private val rows = IntArray(MAX_BUTTONS_PER_COLUMN)
    private var aboutToClose = false

    override fun init() {
        // Selection Button
        selection = PositionAwareImageButton(
            0, 0,
            (width - backgroundWidth) / 2 + SPACING - 2, (height - backgroundHeight) / 2 + SPACING - 2,
            buttonWidth + 4, buttonHeight + 4,
            0, 0, 0,
            selectionTexture, buttonWidth + 4, buttonHeight + 4
        ) {}

        // Pokedex Button
        buttons.add(posImageButtonOf(0, 0, pokedex, this::onPressPokedex))

        // Bag Button
        buttons.add(posImageButtonOf(1, 0, bag, this::onPressBag))

        // ? Button
        buttons.add(posImageButtonOf(0, 1, question, this::onPressQuestion))

        buttons.forEach { button ->
            rows[button.posY]++ // To know how many buttons are in one row
            addRenderableOnly(button)
        }
        addRenderableWidget(selection)

        super.init()
    }

    override fun keyPressed(pKeyCode: Int, pScanCode: Int, pModifiers: Int): Boolean {
        if(pKeyCode == InputConstants.KEY_RIGHT || pKeyCode == InputConstants.KEY_D) {
            if(currentSelectionPos.first == rows[currentSelectionPos.second] - 1) { // If the current position is the last button in the row
                changeSelectionToPos(0, currentSelectionPos.second) // Reset to first button of row
            } else {
                changeSelectionToPos(currentSelectionPos.first + 1, currentSelectionPos.second) // Go one Button to the right
            }
        }
        if(pKeyCode == InputConstants.KEY_LEFT || pKeyCode == InputConstants.KEY_A) {
            if(currentSelectionPos.first == 0) { // If the current button is the first one in the row
                changeSelectionToPos(rows[currentSelectionPos.second] - 1, currentSelectionPos.second) // Go to the last Button in the row
            } else {
                changeSelectionToPos(currentSelectionPos.first - 1, currentSelectionPos.second) // Go one Button to the right
            }
        }
        if(pKeyCode == InputConstants.KEY_UP || pKeyCode == InputConstants.KEY_W) {
            if(currentSelectionPos.second == 0) { // If the current Button is in the uppermost row
                val x = currentSelectionPos.first
                val y = rows.indexOfLast { it != 0 } // Can have entries with 0 (0 representing that there is now button in that column) so the last filled one
                if(x > rows[y] - 1) { // If the row above (can also be the last due to going up in the first row puts you in the last row) has fewer buttons than the current one also go back one button
                    changeSelectionToPos(x - 1, y)
                } else {
                    changeSelectionToPos(x, y)
                }
            } else {
                changeSelectionToPos(currentSelectionPos.first, currentSelectionPos.second - 1)
            }
        }
        if(pKeyCode == InputConstants.KEY_DOWN || pKeyCode == InputConstants.KEY_S) {
            if(currentSelectionPos.second == rows.indexOfLast { it != 0 }) { // If the current Button is in the last row
                changeSelectionToPos(currentSelectionPos.first, 0) // Go back to the first row
            } else {
                val x = currentSelectionPos.first
                val y = currentSelectionPos.second + 1
                if(x > rows[y] - 1) { // Same as in up but with down
                    changeSelectionToPos(x - 1, y)
                } else {
                    changeSelectionToPos(x, y)
                }
            }
        }
        if(pKeyCode == InputConstants.KEY_SPACE) {
            buttons.firstOrNull {
                it.posX == currentSelectionPos.first && it.posY == currentSelectionPos.second
            }?.onPress() // Executes the onPress action for the currently selected button
        }
        if(pKeyCode == PokeNavigatorBinding.key.value || pKeyCode == InputConstants.KEY_LSHIFT || pKeyCode == InputConstants.KEY_RSHIFT) {
            aboutToClose = true // This is needed so the UI doesn't open itself afterwards again (Closing with same key as opening) -> KeyReleased
        }

        return super.keyPressed(pKeyCode, pScanCode, pModifiers)
    }

    override fun keyReleased(pKeyCode: Int, pScanCode: Int, pModifiers: Int): Boolean {
        if((pKeyCode == PokeNavigatorBinding.key.value || pKeyCode == InputConstants.KEY_LSHIFT || pKeyCode == InputConstants.KEY_RSHIFT) && aboutToClose) {
            Minecraft.getInstance().setScreen(null) // So we only close if the Key was released
        }
        return super.keyReleased(pKeyCode, pScanCode, pModifiers)
    }

    /**
     * Change the currentSelection thingy to the position of another Button
     */
    private fun changeSelectionToPos(posX: Int, posY: Int) {
        val newWidth = getWidthForPos(posX) - 2
        val newHeight = getHeightFor(posY) - 2
        selection.setPosition(newWidth, newHeight)
        currentSelectionPos = Pair(posX, posY)
    }

    /**
     * Method for calculating the width based on the background, spacing and button position
     */
    private fun getWidthForPos(posX: Int): Int {
        return (width - backgroundWidth) / 2 + (posX + 1) * SPACING + (posX) * buttonWidth
    }

    /**
     * Method for calculating the height based on the background, spacing and button position
     */
    private fun getHeightFor(posY: Int): Int {
        return (height - backgroundHeight) / 2 + (posY + 1) * SPACING + (posY) * buttonHeight
    }

    /**
     * To simplify creating PositionAwareImageButtons
     */
    private fun posImageButtonOf(
        posX: Int,
        posY: Int,
        resourceLocation: ResourceLocation,
        onPress: Button.OnPress
    ): PositionAwareImageButton {
        return PositionAwareImageButton(
            posX, posY,
            getWidthForPos(posX), getHeightFor(posY),
            buttonWidth, buttonHeight,
            0, 0, 0,
            resourceLocation, buttonWidth, buttonHeight,
            onPress
        )
    }

    /**
     * What should happen on Button press - START
     */

    private fun onPressPokedex(button: Button) {
        println("Pressed Pokedex")
    }

    private fun onPressBag(button: Button) {
        println("Pressed Bag")
    }

    private fun onPressQuestion(button: Button) {
        println("Pressed Question")
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
        RenderSystem.setShader(GameRenderer::getPositionTexShader)
        RenderSystem.setShaderTexture(0, background)
        RenderSystem.enableDepthTest()
        blit(pMatrixStack,
            (width - backgroundWidth) / 2, (height - backgroundHeight) / 2,
            0F, 0F, backgroundWidth, backgroundHeight, backgroundWidth, backgroundHeight)

        super.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks)
    }

    /**
     * Whether the screen should pause the game or not
     */
    override fun isPauseScreen(): Boolean {
        return true
    }
}