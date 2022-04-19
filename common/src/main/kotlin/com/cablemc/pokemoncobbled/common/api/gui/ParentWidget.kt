package com.cablemc.pokemoncobbled.common.api.gui

import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.Widget
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component

/**
 * This class adds children-awareness to a Widget similar like a Screen
 * (otherwise the Widgets do not react on click/hover)
 */
abstract class ParentWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    component: Component
): Widget, AbstractWidget(pX, pY, pWidth, pHeight, component) {

    private val children: MutableList<GuiEventListener> = mutableListOf()

    /**
     * Adds Widget to the children list
     */
    protected fun addWidget(widget: GuiEventListener) {
        children.add(widget)
    }

    /**
     * Removes Widget from the children list
     */
    protected fun removeWidget(widget: GuiEventListener) {
        children.remove(widget)
    }

    override fun mouseMoved(pMouseX: Double, pMouseY: Double) {
        children.forEach {
            it.mouseMoved(pMouseX, pMouseY)
        }
        super.mouseMoved(pMouseX, pMouseY)
    }

    override fun mouseScrolled(pMouseX: Double, pMouseY: Double, pDelta: Double): Boolean {
        return children.any {
            it.mouseScrolled(pMouseX, pMouseY, pDelta)
        } || super.mouseScrolled(pMouseX, pMouseY, pDelta)
    }

    override fun mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        return children.any {
            it.mouseClicked(pMouseX, pMouseY, pButton)
        }
    }

    override fun mouseReleased(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        return children.any {
            it.mouseReleased(pMouseX, pMouseY, pButton)
        } || super.mouseReleased(pMouseX, pMouseY, pButton)
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, f: Double, g: Double): Boolean {
        return children.any {
            it.mouseDragged(mouseX, mouseY, button, f, g)
        }
    }

    override fun keyPressed(pKeyCode: Int, pScanCode: Int, pModifiers: Int): Boolean {
        return children.any {
            it.keyPressed(pKeyCode, pScanCode, pModifiers)
        } || super.keyPressed(pKeyCode, pScanCode, pModifiers)
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
}