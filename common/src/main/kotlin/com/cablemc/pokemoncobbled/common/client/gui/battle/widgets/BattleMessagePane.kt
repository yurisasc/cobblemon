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
import net.minecraft.util.math.MathHelper.ceil

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
    ceil(UNDERLAY_WIDTH),
    ceil(UNDERLAY_HEIGHT),
    1,
    1 + ceil(UNDERLAY_HEIGHT),
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
        get() = client.window.scaledWidth / 2 + 13
    val appropriateY: Int
        get() = client.window.scaledHeight - 100

    fun correctSize() {
        updateSize(ceil(UNDERLAY_WIDTH), ceil(UNDERLAY_HEIGHT), appropriateY, appropriateY + ceil(UNDERLAY_HEIGHT) + 2)
        setLeftPos(appropriateX)
    }

    companion object {
        const val LINE_HEIGHT = 10
        const val LINE_WIDTH = 210
        const val FRAME_WIDTH_TO_HEIGHT = 604F / 125
        const val FRAME_WIDTH = 188F
        const val FRAME_HEIGHT = FRAME_WIDTH / FRAME_WIDTH_TO_HEIGHT
        const val UNDERLAY_WIDTH_TO_HEIGHT = 580F / 100
        const val UNDERLAY_WIDTH = 188F
        const val UNDERLAY_HEIGHT = UNDERLAY_WIDTH / UNDERLAY_WIDTH_TO_HEIGHT
        private val battleMessagePaneBackgroundResource = cobbledResource("ui/battle/battle_log_underlay.png")
        private val battleMessagePaneBackgroundExpandedResource = cobbledResource("ui/battle/battle_log_expanded_underlay.png")
        private val battleMessagePaneFrameResource = cobbledResource("ui/battle/battle_log_base.png")
        private val battleMessagePaneFrameExpandedResource = cobbledResource("ui/battle/battle_log_base_expanded.png")
    }

    override fun addEntry(entry: BattleMessageLine): Int {
        return super.addEntry(entry)
    }

    override fun getRowWidth(): Int {
        return 80
    }

    override fun getScrollbarPositionX(): Int {
        return left + width - 22
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
            texture = battleMessagePaneBackgroundResource,
            x = left,
            y = top + 2,
            height = UNDERLAY_HEIGHT,
            width = UNDERLAY_WIDTH - 2,
            alpha = 0.7
        )
        blitk(
            matrixStack = poseStack,
            texture = battleMessagePaneFrameResource,
            x = left - 2,
            y = top - 1,
            height = FRAME_HEIGHT,
            width = FRAME_WIDTH
        )

        RenderSystem.enableScissor(scaleIt(left + 2), scaleIt(98 - UNDERLAY_HEIGHT), scaleIt(width - 4), scaleIt(height - 2))
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
            drawScaledText(poseStack, line, rowLeft - 52, rowTop, 0.8F, 0.8F)
        }
    }
}