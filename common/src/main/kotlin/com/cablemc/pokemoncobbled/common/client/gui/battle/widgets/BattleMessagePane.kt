package com.cablemc.pokemoncobbled.common.client.gui.battle.widgets

import com.cablemc.pokemoncobbled.common.api.gui.blitk
import com.cablemc.pokemoncobbled.common.api.text.text
import com.cablemc.pokemoncobbled.common.client.battle.ClientBattleMessageQueue
import com.cablemc.pokemoncobbled.common.client.gui.battle.BattleGUI
import com.cablemc.pokemoncobbled.common.client.render.drawScaledText
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.OrderedText

/**
 * Pane for seeing and interacting with battle messages.
 *
 * @author Hiroku
 * @since June 24th, 2022
 */
class BattleMessagePane(
    val battleGUI: BattleGUI,
    val messageQueue: ClientBattleMessageQueue
): AlwaysSelectedEntryListWidget<BattleMessagePane.BattleMessageLine>(
    MinecraftClient.getInstance(),
    TEXT_BOX_WIDTH, // width
    TEXT_BOX_HEIGHT, // height
    1, // top
    1 + TEXT_BOX_HEIGHT, // bottom
    LINE_HEIGHT
) {
    init {
        correctSize()
        setRenderHorizontalShadows(false)
        setRenderBackground(false)
        setRenderSelection(false)

        messageQueue.subscribe {
            val fullyScrolledDown = maxScroll - scrollAmount < 10
            addEntry(BattleMessageLine(this, it))
            if (fullyScrolledDown) {
                scrollAmount = maxScroll.toDouble()
            }
        }
    }

    val appropriateX: Int
        get() = client.window.scaledWidth - (FRAME_WIDTH + 12)
    val appropriateY: Int
        get() = client.window.scaledHeight - 85

    fun correctSize() {
        updateSize(TEXT_BOX_WIDTH, TEXT_BOX_HEIGHT, appropriateY + 6, appropriateY + 6 + TEXT_BOX_HEIGHT)
        setLeftPos(appropriateX)
    }

    companion object {
        const val LINE_HEIGHT = 10
        const val LINE_WIDTH = 142
        const val FRAME_WIDTH = 169
        const val FRAME_HEIGHT = 55
        const val TEXT_BOX_WIDTH = 153
        const val TEXT_BOX_HEIGHT = 46

        private val battleMessagePaneFrameResource = cobbledResource("ui/battle/battle_log_base.png")
    }

    override fun addEntry(entry: BattleMessageLine): Int {
        return super.addEntry(entry)
    }

    override fun getRowWidth(): Int {
        return 80
    }

    override fun getScrollbarPositionX(): Int {
        return left + 154
    }

    override fun getScrollAmount(): Double {
        return super.getScrollAmount()
    }

    private fun scaleIt(i: Number): Int {
        return (client.window.scaleFactor * i.toFloat()).toInt()
    }

    override fun render(poseStack: MatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        correctSize()
        blitk(
            matrixStack = poseStack,
            texture = battleMessagePaneFrameResource,
            x = left,
            y = appropriateY,
            height = FRAME_HEIGHT,
            width = FRAME_WIDTH
        )

        RenderSystem.enableScissor(scaleIt(left + 5), scaleIt(33), scaleIt(width), scaleIt(height))
        super.render(poseStack, mouseX, mouseY, partialTicks)
        RenderSystem.disableScissor()
    }

    class BattleMessageLine(val pane: BattleMessagePane, val line: OrderedText) : Entry<BattleMessageLine>() {
        override fun getNarration() = "".text()
        override fun render(
            poseStack: MatrixStack,
            index: Int,
            rowTop: Int,
            rowLeft: Int,
            rowWidth: Int,
            rowHeight: Int,
            mouseX: Int,
            mouseY: Int,
            isHovered: Boolean,
            partialTicks: Float
        ) {
            drawScaledText(
                poseStack,
                line,
                rowLeft - 29,
                rowTop - 2
            )
        }
    }
}