package com.cablemc.pokemoncobbled.common.client.gui.pc

import com.cablemc.pokemoncobbled.common.api.storage.pc.PCPosition
import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.SoundlessWidget
import com.cablemc.pokemoncobbled.common.client.storage.ClientPC
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralText

class PCWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    private val pcGui: PCGui,
    private val pc: ClientPC
) : SoundlessWidget(pX, pY, pWidth, pHeight, LiteralText("PCWidget")) {

    var box = 0
        set(value) {
            // If value is within min and max
            field = if (value > 0 && value < pc.boxes.size) {
                value
            }
            // If value is less than zero, wrap around to end.
            else if (value < 0) {
                pc.boxes.size - 1;
            }
            // Else it's greater than max, wrap around to start.
            else {
                0
            }
            this.setupMemberWidgets()
        }
    private val pcWidgets = arrayListOf<PCBoxMemberWidget>()

    init {
        this.setupMemberWidgets()
    }

    private fun setupMemberWidgets() {
        this.pcWidgets.clear()
        var index = 0;

        for (row in 1..5) {
            for (col in 1..6) {
                PCBoxMemberWidget(
                    x = x + (col-1) * 29,
                    y = y + (row-1) * 29,
                    pcGui = pcGui,
                    pc = pc,
                    pokemon = pc.get(PCPosition(box, index)),
                    index = index
                ).also {  widget ->
                    this.addWidget(widget)
                    this.pcWidgets.add(widget)
                }
                index++
            }
        }
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        this.pcWidgets.forEach { widget -> widget.render(matrices, mouseX, mouseY, delta) }
    }

}