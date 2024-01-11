package com.cobblemon.mod.common.client.gui.tm

import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.tms.TechnicalMachine
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget
import net.minecraft.text.Text

class TMScrollingList(
    val x: Int,
    val y: Int,
    val parent: TMMHandledScreen
) : AlwaysSelectedEntryListWidget<TMScrollingList.TMScrollingListEntry> (
    MinecraftClient.getInstance(),
    WIDTH, // width
    HEIGHT, // height
    0, // top
    HEIGHT, // bottom
    SLOT_HEIGHT + SLOT_SPACING
) {

    companion object {
        const val WIDTH = 140
        const val HEIGHT = 78
        const val SLOT_WIDTH = 140
        const val SLOT_HEIGHT = 20
        const val SLOT_SPACING = 3
        const val SCALE = 0.5f
    }

    private var scrolling = false

    override fun getRowWidth() = SLOT_WIDTH

    init {
        correctSize()
        setRenderHorizontalShadows(false)
        setRenderBackground(false)
        setRenderSelection(false)

        parent.tmList.subscribeIncludingCurrent {
            val children = children()
            val newEntries = it.filter { pk -> children.none { it.tm.id() == pk.id() } }
            val removedEntries = children().filter { pk -> it.none { it.id() == pk.tm.id() } }

            removedEntries.forEach(this::removeEntry)
            newEntries.forEach { addEntry(TMScrollingListEntry(it, parent)) }
        }
    }

    override fun getScrollbarPositionX() = left + width - 3

    private fun correctSize() {
        updateSize(
            WIDTH,
            HEIGHT, y + 1, (y + 1) + (HEIGHT - 2))
        setLeftPos(x)
    }

    public override fun addEntry(entry: TMScrollingListEntry) = super.addEntry(entry)
    public override fun removeEntry(entry: TMScrollingListEntry) = super.removeEntry(entry)

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        correctSize()

        context.enableScissor(
            left,
            top + 1,
            left + width,
            top + 1 + height
        )

        super.render(context, mouseX, mouseY, delta)
        context.disableScissor()
    }

    fun isHovered(mouseX: Double, mouseY: Double): Boolean {
        println(mouseX.toFloat() in (x.toFloat()..(x.toFloat() + WIDTH)) && mouseY.toFloat() in (y.toFloat()..(y.toFloat() + HEIGHT)))
        return mouseX.toFloat() in (x.toFloat()..(x.toFloat() + WIDTH)) && mouseY.toFloat() in (y.toFloat()..(y.toFloat() + HEIGHT))
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        updateScrollingState(mouseX, mouseY)
        if (scrolling) {
            focused = getEntryAtPosition(mouseX, mouseY)
            isDragging = true
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        if (scrolling) {
            if (mouseY < top) {
                scrollAmount = 0.0
            } else if (mouseY > bottom) {
                scrollAmount = maxScroll.toDouble()
            } else {
                scrollAmount += deltaY
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
    }

    private fun updateScrollingState(mouseX: Double, mouseY: Double) {
        scrolling = mouseX >= this.scrollbarPositionX.toDouble()
                && mouseX < (this.scrollbarPositionX + 3).toDouble()
                && mouseY >= top
                && mouseY < bottom
    }

    class TMScrollingListEntry(val tm: TechnicalMachine, private val parent: TMMHandledScreen) : Entry<TMScrollingListEntry>() {
        override fun render(
            context: DrawContext,
            index: Int,
            rowTop: Int,
            rowLeft: Int,
            rowWidth: Int,
            rowHeight: Int,
            mouseX: Int,
            mouseY: Int,
            isHovered: Boolean,
            tickDelta: Float
        ) {
            val x = rowLeft
            val y = rowTop
            val matrixStack = context.matrices

            var iteration = 1
            val startY = 40
            val offset = 20

            parent.tmList.get().forEach { tm ->
                TMListingButton(
                    pX = x - 1,
                    pY = startY + (offset * iteration),
                    onPress = {
                        parent.selectedTM = tm
                        parent.inventory.player.playSound(CobblemonSounds.GUI_CLICK, 1f, 1f)
                    },
                    tm = tm
                ).render(context, mouseX, mouseY, tickDelta)
                iteration++
            }
        }


        override fun getNarration(): Text {
            TODO("Not yet implemented")
        }

    }

}