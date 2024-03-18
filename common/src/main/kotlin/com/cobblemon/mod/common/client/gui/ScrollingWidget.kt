package com.cobblemon.mod.common.client.gui

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.ParentElement
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget
import net.minecraft.client.gui.widget.EntryListWidget
import net.minecraft.util.math.MathHelper

abstract class ScrollingWidget<T : AlwaysSelectedEntryListWidget.Entry<T>>(
    top : Int = 0,
    left: Int = 0,
    width : Int = 10,
    height: Int = 10,
    slotHeight : Int = 10,
    val scrollBarWidth : Int = 5
) : AlwaysSelectedEntryListWidget<T>(
    MinecraftClient.getInstance(),
    width, // Width
    height, // Height
    top, // Top
    top + height, // Bottom
    slotHeight // Slot Height
){
    init {
        setRenderHorizontalShadows(false)
        setRenderBackground(false)
        setRenderSelection(false)

        updateSize(width, height, top, top + height)
        setLeft(left)
    }

    override fun updateSize(width: Int, height: Int, top: Int, bottom: Int) {
        this.width = width
        this.height = height
        this.top = top
        this.bottom = bottom
        this.right = left + width
    }

    fun setLeft(left: Int){
        this.left = left
        this.right = left + width
    }

    override fun renderList(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        val i = this.rowLeft
        val j = this.rowWidth
        val k = this.itemHeight
        val l = this.entryCount

        for (m in 0 until l) {
            val n = this.getRowTop(m)
            val o = this.getRowBottom(m)
            if (o >= this.top && n <= this.bottom) {
                this.renderEntry(context!!, mouseX, mouseY, delta, m, i, n, j, k)
            }
        }
    }

    override fun renderEntry(
        context: DrawContext,
        mouseX: Int,
        mouseY: Int,
        delta: Float,
        index: Int,
        x: Int,
        y: Int,
        entryWidth: Int,
        entryHeight: Int
    ) {
        val entry =  this.getEntry(index)
        entry.render(
            context, index, y, x, entryWidth, entryHeight, mouseX, mouseY,
            hoveredEntry == entry, delta
        )
    }

    override fun getRowLeft(): Int {
        return this.left
    }

    override fun getRowRight(): Int {
        return this.rowLeft + this.rowWidth
    }

    override fun getRowWidth(): Int {
        return this.width - scrollBarWidth
    }

    override fun getRowTop(index: Int): Int {
        return this.top - scrollAmount.toInt() + (index * this.itemHeight)
    }

    override fun getRowBottom(index: Int): Int {
        return this.getRowTop(index) + this.itemHeight
    }

    override fun getScrollbarPositionX() = this.left + this.width - this.scrollBarWidth

    fun getSlotAtPosition(x: Double, y: Double): T? {
        val i = this.rowWidth / 2
        val j = this.left + this.width / 2
        val k = j - i
        val l = j + i
        val m = MathHelper.floor(y - top.toDouble()) - this.headerHeight + scrollAmount.toInt()
        val n = m / this.itemHeight
        return if ((x < this.scrollbarPositionX.toDouble() && x >= k.toDouble() && x <= l.toDouble() && n >= 0 && m >= 0) && n < this.entryCount) children()[n] as T else null
    }
    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        this.updateScrollingState(mouseX, mouseY, button)
        if (!this.isMouseOver(mouseX, mouseY)) {
            return false
        } else {
            val entry: T? = this.getSlotAtPosition(mouseX, mouseY)
            if (entry != null) {
                if (entry.mouseClicked(mouseX, mouseY, button)) {
                    val entry2: T? = this.focused
                    if (entry2 !== entry && entry2 is ParentElement) {
                        val parentElement = entry2 as ParentElement
                        parentElement.focused = null as Element?
                    }

                    this.setFocused(entry)
                    this.isDragging = true
                    return true
                }
            } else if (button == 0) {
                this.clickedHeader(
                    (mouseX - (this.left + this.width / 2 - this.rowWidth / 2).toDouble()).toInt(),
                    (mouseY - top.toDouble()).toInt() + scrollAmount.toInt()
                )
                return true
            }

            // Cant do the default minecraft thing since scrolling is private
            // return this.scrolling
            return false
        }
    }

    abstract class Slot<T : Slot<T>>(): Entry<T>() {
        // Override render to show each individual element
    }
}