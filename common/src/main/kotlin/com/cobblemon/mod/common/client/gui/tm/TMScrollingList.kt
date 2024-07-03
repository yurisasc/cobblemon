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
    SLOT_HEIGHT// + SLOT_SPACING
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

    override fun drawMenuListBackground(context: DrawContext) {}
    override fun drawHeaderAndFooterSeparators(context: DrawContext) {}
    override fun drawSelectionHighlight(context: DrawContext, y: Int, entryWidth: Int, entryHeight: Int, borderColor: Int, fillColor: Int) {}

    init {
        correctSize()

        parent.tmList.subscribeIncludingCurrent { tmList ->
            val currentEntries = children()
            val newTMs = tmList.filter { tm -> currentEntries.none { it.tm.id == tm.id } }
            val removedTMs = currentEntries.filter { entry -> tmList.none { it.id == entry.tm.id } }

            removedTMs.forEach(this::removeEntry)
            newTMs.forEach { tm -> addEntry(TMScrollingListEntry(tm, parent)) }
        }
    }

    override fun getScrollbarX() = x + width - 3

    private fun correctSize() {
        setDimensionsAndPosition(
            WIDTH,
            HEIGHT,
            x,
            y + 2
        )
//        updateSize(
//            WIDTH,
//            HEIGHT, y - 2, (y + 2) + (HEIGHT - 2)
//        )
//        setLeftPos(x)
    }

    public override fun addEntry(entry: TMScrollingListEntry) = super.addEntry(entry)
    public override fun removeEntry(entry: TMScrollingListEntry) = super.removeEntry(entry)

    override fun renderWidget(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        correctSize()

        context.enableScissor(
            x,
            y + 5,
            x + width,
            y + 1 + height
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
            if (mouseY < y) {
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
        scrolling = mouseX >= this.scrollbarX.toDouble()
                && mouseX < (this.scrollbarX + 3).toDouble()
                && mouseY >= y
                && mouseY < bottom
    }

    class TMScrollingListEntry(val tm: TechnicalMachine, private val parent: TMMHandledScreen) : Entry<TMScrollingListEntry>() {

        private val tmButton: TMListingButton = TMListingButton(
                pX = 0,
                pY = 0,
                onPress = {
                    parent.selectedTM = tm
                    parent.inventory.player.playSound(CobblemonSounds.GUI_CLICK, 1f, 1f)
                },
                tm = tm
        )
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

            // Render just this TMListingButton
            tmButton.setPosition(x + 1, y)
            tmButton.render(context, mouseX, mouseY, tickDelta)
        }

        override fun mouseClicked(mouseX: Double, mouseY: Double, delta: Int): Boolean {
            if (tmButton.isHovered(mouseX, mouseY)) {
                tmButton.onPress()
                return true
            }
            return false
        }

        override fun getNarration(): Text {
            TODO("Not yet implemented")
        }

    }

}