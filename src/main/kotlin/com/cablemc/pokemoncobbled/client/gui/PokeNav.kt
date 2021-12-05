package com.cablemc.pokemoncobbled.client.gui

import com.cablemc.pokemoncobbled.client.keybinding.PokeNavigatorBinding
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.mojang.blaze3d.platform.InputConstants
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.ImageButton
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.network.chat.TextComponent
import net.minecraft.resources.ResourceLocation

class PokeNav: Screen(TextComponent("PokeNav")) {

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
        private val background = ResourceLocation("pokemoncobbled", "ui/pokenav/test.png")
        private val pokedex = ResourceLocation(PokemonCobbled.MODID, "ui/pokenav/pokedex.png")
        private val bag = ResourceLocation(PokemonCobbled.MODID, "ui/pokenav/bag.png")
        private val question = ResourceLocation(PokemonCobbled.MODID, "ui/pokenav/question.png")
        private val selectionTexture = ResourceLocation(PokemonCobbled.MODID, "ui/pokenav/selection.png")
    }

    private val buttons = mutableListOf<PositionAwareImageButton>()
    private lateinit var selection: ImageButton
    private var currentSelectionPos: Pair<Int, Int> = Pair(0, 0)
    // ^ always start at the first entry
    private val rows = IntArray(MAX_BUTTONS_PER_COLUMN)
    private var aboutToClose = false

    override fun init() {
        // Selection
        selection = PositionAwareImageButton(
            0, 0,
            (width - backgroundWidth) / 2 + SPACING - 2, (height - backgroundHeight) / 2 + SPACING - 2,
            buttonWidth + 4, buttonHeight + 4,
            0, 0, 0,
            selectionTexture, buttonWidth + 4, buttonHeight + 4
        ) {}

        // Pokedex
        buttons.add(posImageButtonOf(0, 0, pokedex, this::onPressPokedex))

        // Bag
        buttons.add(posImageButtonOf(1, 0, bag, this::onPressBag))

        // ?
        buttons.add(posImageButtonOf(0, 1, question, this::onPressQuestion))

        buttons.forEach { button ->
            rows[button.posY]++
            addRenderableOnly(button)
        }
        addRenderableWidget(selection)

        super.init()
    }

    override fun keyPressed(pKeyCode: Int, pScanCode: Int, pModifiers: Int): Boolean {
        if(pKeyCode == InputConstants.KEY_RIGHT || pKeyCode == InputConstants.KEY_D) {
            if(currentSelectionPos.first == MAX_BUTTONS_PER_ROW || currentSelectionPos.first == rows[currentSelectionPos.second] - 1) {
                changeSelectionToPos(0, currentSelectionPos.second)
            } else {
                changeSelectionToPos(currentSelectionPos.first + 1, currentSelectionPos.second)
            }
        }
        if(pKeyCode == InputConstants.KEY_LEFT || pKeyCode == InputConstants.KEY_A) {
            if(currentSelectionPos.first == 0) {
                changeSelectionToPos(rows[currentSelectionPos.second] - 1, currentSelectionPos.second)
            } else {
                changeSelectionToPos(currentSelectionPos.first - 1, currentSelectionPos.second)
            }
        }
        if(pKeyCode == InputConstants.KEY_UP || pKeyCode == InputConstants.KEY_W) {
            if(currentSelectionPos.second == 0) {
                val x = currentSelectionPos.first
                val y = rows.indexOfLast { it != 0 }
                if(x > rows[y] - 1) {
                    changeSelectionToPos(x - 1, y)
                } else {
                    changeSelectionToPos(x, y)
                }
            } else {
                changeSelectionToPos(currentSelectionPos.first, currentSelectionPos.second - 1)
            }
        }
        if(pKeyCode == InputConstants.KEY_DOWN || pKeyCode == InputConstants.KEY_S) {
            if(currentSelectionPos.second == rows.indexOfLast { it != 0 }) {
                changeSelectionToPos(currentSelectionPos.first, 0)
            } else {
                val x = currentSelectionPos.first
                val y = currentSelectionPos.second + 1
                if(x > rows[y] - 1) {
                    changeSelectionToPos(x - 1, y)
                } else {
                    changeSelectionToPos(x, y)
                }
            }
        }
        if(pKeyCode == InputConstants.KEY_SPACE) {
            buttons.firstOrNull {
                it.posX == currentSelectionPos.first && it.posY == currentSelectionPos.second
            }?.onPress()
        }
        if(pKeyCode == PokeNavigatorBinding.key.value || pKeyCode == InputConstants.KEY_LSHIFT || pKeyCode == InputConstants.KEY_RSHIFT) {
            aboutToClose = true
        }

        return super.keyPressed(pKeyCode, pScanCode, pModifiers)
    }

    override fun keyReleased(pKeyCode: Int, pScanCode: Int, pModifiers: Int): Boolean {
        if((pKeyCode == PokeNavigatorBinding.key.value || pKeyCode == InputConstants.KEY_LSHIFT || pKeyCode == InputConstants.KEY_RSHIFT) && aboutToClose) {
            Minecraft.getInstance().setScreen(null)
        }
        return super.keyReleased(pKeyCode, pScanCode, pModifiers)
    }

    private fun changeSelectionToPos(posX: Int, posY: Int) {
        val newWidth = getWidthForPos(posX) - 2
        val newHeight = getHeightFor(posY) - 2
        selection.setPosition(newWidth, newHeight)
        currentSelectionPos = Pair(posX, posY)
    }

    private fun getWidthForPos(posX: Int): Int {
        return (width - backgroundWidth) / 2 + (posX + 1) * SPACING + (posX) * buttonWidth
    }

    private fun getHeightFor(posY: Int): Int {
        return (height - backgroundHeight) / 2 + (posY + 1) * SPACING + (posY) * buttonHeight
    }

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

    private fun onPressPokedex(button: Button) {
        println("Pressed Pokedex")
    }

    private fun onPressBag(button: Button) {
        println("Pressed Bag")
    }

    private fun onPressQuestion(button: Button) {
        println("Pressed Question")
    }

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

    override fun isPauseScreen(): Boolean {
        return true
    }
}