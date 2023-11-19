package com.cobblemon.mod.common.client.gui.pokedex

import com.cobblemon.mod.common.client.storage.ClientPokedex
import com.cobblemon.mod.common.pokedex.PokedexEntry
import com.cobblemon.mod.common.util.asTranslated
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack

class PokedexScreen private constructor(): Screen("cobblemon.ui.pokedex.title".asTranslated()) {
    var pokedex = mutableListOf<PokedexEntry>()

    companion object {
        // Size of UI at scale 1
        private const val BASE_WIDTH = 200
        private const val BASE_HEIGHT = 175

        // Resources
        private val base = cobblemonResource("ui/starterselection/starterselection_base.png")
        private val baseUnderlay = cobblemonResource("ui/starterselection/starterselection_base_underlay.png")
        private val baseFrame = cobblemonResource("ui/starterselection/starterselection_base_frame.png")

        // Type Backgrounds
        private val singleTypeBackground = cobblemonResource("ui/starterselection/starterselection_type_slot1.png")
        private val doubleTypeBackground = cobblemonResource("ui/starterselection/starterselection_type_slot2.png")
    }

    override fun init() {
        super.init()

        val x = (width - PokedexScreen.BASE_WIDTH) / 2
        val y = (height - PokedexScreen.BASE_HEIGHT) / 2
    }

    constructor(pokedex: MutableList<PokedexEntry>): this() {
        this.pokedex = pokedex
    }
    constructor(clientPokedex: ClientPokedex): this() {
        this.pokedex = clientPokedex.entries
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        val x = (width - PokedexScreen.BASE_WIDTH) / 2
        val y = (height - PokedexScreen.BASE_HEIGHT) / 2

        // Render the rest
        super.render(matrices, mouseX, mouseY, delta)
    }
}