package com.cablemc.pokemoncobbled.client.gui

import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.Widget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component

abstract class ParentWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    component: Component
): Widget, AbstractWidget(pX, pY, pWidth, pHeight, component) {

    private val children: MutableList<AbstractWidget> = mutableListOf()

    override fun mouseMoved(pMouseX: Double, pMouseY: Double) {
        children.forEach {
            it.mouseMoved(pMouseX, pMouseY)
        }
        super.mouseMoved(pMouseX, pMouseY)
    }

    override fun mouseScrolled(pMouseX: Double, pMouseY: Double, pDelta: Double): Boolean {
        children.forEach {
            it.mouseScrolled(pMouseX, pMouseY, pDelta)
        }
        return super.mouseScrolled(pMouseX, pMouseY, pDelta)
    }

    override fun mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        children.forEach {
            it.mouseClicked(pMouseX, pMouseY, pButton)
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton)
    }

    override fun keyPressed(pKeyCode: Int, pScanCode: Int, pModifiers: Int): Boolean {
        children.forEach {
            it.keyPressed(pKeyCode, pScanCode, pModifiers)
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers)
    }

    override fun keyReleased(pKeyCode: Int, pScanCode: Int, pModifiers: Int): Boolean {
        children.forEach {
            it.keyReleased(pKeyCode, pScanCode, pModifiers)
        }
        return super.keyReleased(pKeyCode, pScanCode, pModifiers)
    }

    override fun charTyped(pCodePoint: Char, pModifiers: Int): Boolean {
        children.forEach {
            it.charTyped(pCodePoint, pModifiers)
        }
        return super.charTyped(pCodePoint, pModifiers)
    }

    override fun updateNarration(pNarrationElementOutput: NarrationElementOutput) {
    }

    protected fun addWidget(widget: AbstractWidget) {
        children.add(widget)
    }

    protected fun removeWidget(widget: AbstractWidget) {
        children.remove(widget)
    }
}