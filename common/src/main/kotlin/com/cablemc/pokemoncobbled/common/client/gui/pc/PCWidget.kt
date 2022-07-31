package com.cablemc.pokemoncobbled.common.client.gui.pc

import com.cablemc.pokemoncobbled.common.api.storage.pc.PCPosition
import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.SoundlessWidget
import com.cablemc.pokemoncobbled.common.client.storage.ClientPC
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralText

class PCWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    pcGui: PCGui,
    pc: ClientPC
) : SoundlessWidget(pX, pY, pWidth, pHeight, LiteralText("PCWidget")) {

    var box = 0
    private val pcWidgets = arrayListOf<PCBoxMemberWidget>()

    init {
        var index = 0;

        for (row in 1..5) {
            for (col in 1..6) {
                PCBoxMemberWidget(
                    x = pX + (col-1) * 29,
                    y = pY + (row-1) * 29,
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