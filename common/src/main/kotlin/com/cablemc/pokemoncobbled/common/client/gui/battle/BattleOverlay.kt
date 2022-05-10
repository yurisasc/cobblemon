package com.cablemc.pokemoncobbled.common.client.gui.battle

import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.hud.InGameHud
import net.minecraft.client.util.math.MatrixStack
import java.lang.Double.max
import java.lang.Double.min

class BattleOverlay : InGameHud(MinecraftClient.getInstance()) {
    companion object {
        const val MAX_OPACITY = 1.0
        const val MIN_OPACITY = 0.3
        const val OPACITY_CHANGE_PER_SECOND = 1.0
    }

    var opacity = MIN_OPACITY

    override fun render(matrices: MatrixStack?, tickDelta: Float) {
        val battle = PokemonCobbledClient.battle ?: return
        opacity = if (battle.minimised) {
            max(opacity - tickDelta * OPACITY_CHANGE_PER_SECOND, MIN_OPACITY)
        } else {
            min(opacity + tickDelta * OPACITY_CHANGE_PER_SECOND, MAX_OPACITY)
        }



        super.render(matrices, tickDelta)
    }
}