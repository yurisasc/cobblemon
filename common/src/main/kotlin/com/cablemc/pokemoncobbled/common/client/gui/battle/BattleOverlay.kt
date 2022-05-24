package com.cablemc.pokemoncobbled.common.client.gui.battle

import com.cablemc.pokemoncobbled.common.api.gui.blitk
import com.cablemc.pokemoncobbled.common.api.gui.drawPortraitPokemon
import com.cablemc.pokemoncobbled.common.api.text.text
import com.cablemc.pokemoncobbled.common.client.CobbledResources
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.battle.ActiveClientBattlePokemon
import com.cablemc.pokemoncobbled.common.client.render.drawScaled
import com.cablemc.pokemoncobbled.common.client.render.getDepletableRedGreen
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.cablemc.pokemoncobbled.common.util.lang
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.hud.InGameHud
import net.minecraft.client.util.math.MatrixStack
import java.lang.Double.max
import java.lang.Double.min
import kotlin.math.roundToInt

class BattleOverlay : InGameHud(MinecraftClient.getInstance()) {
    companion object {
        const val MAX_OPACITY = 1.0
        const val MIN_OPACITY = 0.5
        const val OPACITY_CHANGE_PER_SECOND = 0.1
        const val HORIZONTAL_INSET = 20
        const val VERTICAL_INSET = 10
        const val HORIZONTAL_SPACING = 15
        const val VERTICAL_SPACING = 40
        const val INFO_OFFSET_X = 5

        const val TILE_WIDTH_TO_HEIGHT = 1 / 3.1764705F
        const val TILE_WIDTH = 120
        const val TILE_HEIGHT = TILE_WIDTH * TILE_WIDTH_TO_HEIGHT
        const val PORTRAIT_DIAMETER = 116 / 432F * TILE_WIDTH
        const val PORTRAIT_OFFSET = 10 / 432F * TILE_WIDTH


        val battleInfoBase = cobbledResource("ui/battle/battle_info_base.png")
        val battleInfoBaseFlipped = cobbledResource("ui/battle/battle_info_base_flipped.png")
        val battleInfoUnderlay = cobbledResource("ui/battle/battle_info_underlay.png")
    }

    var opacity = MIN_OPACITY

    override fun render(matrices: MatrixStack, tickDelta: Float) {
        val battle = PokemonCobbledClient.battle ?: return
        opacity = if (battle.minimised) {
            max(opacity - tickDelta * OPACITY_CHANGE_PER_SECOND, MIN_OPACITY)
        } else {
            min(opacity + tickDelta * OPACITY_CHANGE_PER_SECOND, MAX_OPACITY)
        }

        val playerUUID = MinecraftClient.getInstance().player?.uuid ?: return
        val side1 = if (battle.side1.actors.any { it.uuid == playerUUID }) battle.side1 else battle.side2
        val side2 = if (side1 == battle.side1) battle.side2 else battle.side1

        side1.activeClientBattlePokemon.forEachIndexed { index, activeClientBattlePokemon -> drawTile(matrices, tickDelta, activeClientBattlePokemon, true, index) }
        side2.activeClientBattlePokemon.forEachIndexed { index, activeClientBattlePokemon -> drawTile(matrices, tickDelta, activeClientBattlePokemon, false, index) }
    }


    fun drawTile(matrices: MatrixStack, tickDelta: Float, activeBattlePokemon: ActiveClientBattlePokemon, left: Boolean, rank: Int) {
        val mc = MinecraftClient.getInstance()
        fun scaleIt(i: Number): Int {
            return (mc.window.scaleFactor * i.toFloat()).roundToInt()
        }

        matrices.push()
        val battlePokemon = activeBattlePokemon.battlePokemon ?: return
        // First render the underlay
        var x = HORIZONTAL_INSET + rank * HORIZONTAL_SPACING.toFloat()
        val y = VERTICAL_INSET + rank * VERTICAL_SPACING
        if (!left) {
            x = mc.window.scaledWidth - x - TILE_WIDTH
        }
        val invisibleX = if (left) {
            -TILE_WIDTH - 1F
        } else {
            mc.window.scaledWidth.toFloat()
        }

        activeBattlePokemon.invisibleX = invisibleX
        activeBattlePokemon.xDisplacement = x
        activeBattlePokemon.animate(tickDelta)
        x = activeBattlePokemon.xDisplacement

        val portraitStartX = x + if (left) PORTRAIT_OFFSET else { TILE_WIDTH - PORTRAIT_DIAMETER - PORTRAIT_OFFSET }
        blitk(
            matrixStack = matrices,
            texture = battleInfoUnderlay,
            y = y + PORTRAIT_OFFSET,
            x = portraitStartX,
            height = PORTRAIT_DIAMETER,
            width = PORTRAIT_DIAMETER,
            alpha = opacity
        )

        // Second render the Pokémon through the scissors
        RenderSystem.enableScissor(
            scaleIt(portraitStartX),
            mc.window.height - scaleIt(y + PORTRAIT_DIAMETER + PORTRAIT_OFFSET),
            scaleIt(PORTRAIT_DIAMETER.toInt()),
            scaleIt(PORTRAIT_DIAMETER.toInt())
        )
        val matrixStack = MatrixStack()
        matrixStack.translate(
            portraitStartX + PORTRAIT_DIAMETER / 2.0,
            y.toDouble() + PORTRAIT_OFFSET,
            0.0
        )
        drawPortraitPokemon(
            battlePokemon.species,
            battlePokemon.properties.aspects,
            matrixStack,
            scale = 18F,
            reversed = !left,
            state = battlePokemon.state
        )
        RenderSystem.disableScissor()

        // Third render the tile
        val hue = activeBattlePokemon.getHue()
        val r = ((hue shr 16) and 0b11111111) / 255.0
        val g = ((hue shr 8) and 0b11111111) / 255.0
        val b = (hue and 0b11111111) / 255.0
        blitk(
            matrixStack = matrices,
            texture = if (left) battleInfoBase else battleInfoBaseFlipped,
            x = x,
            y = y,
            height = TILE_HEIGHT,
            width = TILE_WIDTH,
            alpha = opacity,
            red = r,
            green = g,
            blue = b
        )

        // Draw labels
        val infoBoxX = x + if (left) { PORTRAIT_DIAMETER + 2 * PORTRAIT_OFFSET + 2 } else { INFO_OFFSET_X.toFloat() }
        mc.textRenderer.drawScaled(
            scaleX = 0.7F,
            scaleY = 0.7F,
            matrixStack = matrices,
            font = CobbledResources.NOTO_SANS_BOLD_SMALL,
            text = battlePokemon.displayName,
            x = infoBoxX,
            y = y + 5,
            colour = 0xFFFFFF,
            shadow = false
        )
        mc.textRenderer.drawScaled(
            scaleX = 0.65F,
            scaleY = 0.65F,
            matrixStack = matrices,
            font = CobbledResources.NOTO_SANS_BOLD_SMALL,
            text = lang("ui.lv"),
            x = infoBoxX + 55,
            y = y + 5,
            colour = 0xFFFFFF,
            shadow = false
        )

        mc.textRenderer.drawScaled(
            scaleX = 0.75F,
            scaleY = 0.75F,
            matrixStack = matrices,
            font = CobbledResources.NOTO_SANS_BOLD_SMALL,
            text = battlePokemon.level.toString().text(),
            x = infoBoxX + 70,
            y = y + 4.3,
            colour = 0xFFFFFF,
            shadow = false,
            centered = true
        )

        val (healthRed, healthGreen) = getDepletableRedGreen(battlePokemon.hpRatio)
        blitk(
            matrixStack = matrices,
            texture = CobbledResources.WHITE,
            x = infoBoxX - 0.5,
            y = y + 13,
            height = 8.5,
            width = battlePokemon.hpRatio * 76.5,
            red = healthRed,
            green = healthGreen,
            blue = 0
        )



        matrices.pop()
    }


}