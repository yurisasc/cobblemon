/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.battle

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.gui.drawPortraitPokemon
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.client.CobblemonClient
import com.cobblemon.mod.common.client.CobblemonResources
import com.cobblemon.mod.common.client.battle.ActiveClientBattlePokemon
import com.cobblemon.mod.common.client.battle.ClientBallDisplay
import com.cobblemon.mod.common.client.gui.battle.widgets.BattleMessagePane
import com.cobblemon.mod.common.client.keybind.boundKey
import com.cobblemon.mod.common.client.keybind.keybinds.PartySendBinding
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.client.render.getDepletableRedGreen
import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import com.cobblemon.mod.common.client.render.models.blockbench.repository.PokeBallModelRepository
import com.cobblemon.mod.common.client.render.models.blockbench.wavefunction.sineFunction
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.pokeball.EmptyPokeBallEntity
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.api.pokemon.gender.Gender
import com.cobblemon.mod.common.pokemon.Species
import com.cobblemon.mod.common.pokemon.status.PersistentStatus
import com.cobblemon.mod.common.util.battleLang
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.lang
import com.mojang.blaze3d.systems.RenderSystem
import java.lang.Double.max
import java.lang.Double.min
import java.util.UUID
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.hud.InGameHud
import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.client.render.DiffuseLighting
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.MutableText
import net.minecraft.util.math.MathHelper.ceil
import net.minecraft.util.math.RotationAxis
import org.joml.Vector3f

class BattleOverlay : InGameHud(MinecraftClient.getInstance(), MinecraftClient.getInstance().itemRenderer) {
    companion object {
        const val MAX_OPACITY = 1.0
        const val MIN_OPACITY = 0.5
        const val OPACITY_CHANGE_PER_SECOND = 0.1
        const val HORIZONTAL_INSET = 12
        const val VERTICAL_INSET = 10
        const val HORIZONTAL_SPACING = 15
        const val VERTICAL_SPACING = 40
        const val INFO_OFFSET_X = 7

        const val TILE_WIDTH = 131
        const val TILE_HEIGHT = 40
        const val PORTRAIT_DIAMETER = 28
        const val PORTRAIT_OFFSET_X = 5
        const val PORTRAIT_OFFSET_Y = 8


        private val PROMPT_TEXT_OPACITY_CURVE = sineFunction(period = 4F, verticalShift = 0.5F, amplitude = 0.5F)

        val battleInfoBase = cobblemonResource("textures/gui/battle/battle_info_base.png")
        val battleInfoBaseFlipped = cobblemonResource("textures/gui/battle/battle_info_base_flipped.png")
        val battleInfoRole = cobblemonResource("textures/gui/battle/battle_info_role.png")
        val battleInfoRoleFlipped = cobblemonResource("textures/gui/battle/battle_info_role_flipped.png")
        val battleInfoUnderlay = cobblemonResource("textures/gui/battle/battle_info_underlay.png")
    }

    var opacity = MIN_OPACITY
    val opacityRatio: Double
        get() = (opacity - MIN_OPACITY) / (MAX_OPACITY - MIN_OPACITY)
    var passedSeconds = 0F

    var lastKnownBattle: UUID? = null
    lateinit var messagePane: BattleMessagePane

    override fun render(context: DrawContext, tickDelta: Float) {
        passedSeconds += tickDelta / 20
        if (passedSeconds > 100) {
            passedSeconds -= 100
        }
        val battle = CobblemonClient.battle ?: return
        opacity = if (battle.minimised) {
            max(opacity - tickDelta * OPACITY_CHANGE_PER_SECOND, MIN_OPACITY)
        } else {
            min(opacity + tickDelta * OPACITY_CHANGE_PER_SECOND, MAX_OPACITY)
        }

        val playerUUID = MinecraftClient.getInstance().player?.uuid ?: return
        val side1 = if (battle.side1.actors.any { it.uuid == playerUUID }) battle.side1 else battle.side2
        val side2 = if (side1 == battle.side1) battle.side2 else battle.side1

        side1.activeClientBattlePokemon.forEachIndexed { index, activeClientBattlePokemon -> drawTile(context, tickDelta, activeClientBattlePokemon, true, index) }
        side2.activeClientBattlePokemon.forEachIndexed { index, activeClientBattlePokemon -> drawTile(context, tickDelta, activeClientBattlePokemon, false, index) }

        if (MinecraftClient.getInstance().currentScreen !is BattleGUI && battle.mustChoose) {
            val textOpacity = PROMPT_TEXT_OPACITY_CURVE(passedSeconds)
            drawScaledText(
                context = context,
                text = battleLang("ui.actions_label", PartySendBinding.boundKey().localizedText),
                x = MinecraftClient.getInstance().window.scaledWidth / 2,
                y = MinecraftClient.getInstance().window.scaledHeight / 5,
                opacity = textOpacity,
                centered = true
            )
        }

        val currentScreen = MinecraftClient.getInstance().currentScreen

        if (currentScreen == null || currentScreen is ChatScreen) {
            if (lastKnownBattle != battle.battleId) {
                lastKnownBattle = battle.battleId
                messagePane = BattleMessagePane(CobblemonClient.battle!!.messages)
            }
            messagePane.opacity = 0.3F
            messagePane.render(context, 0, 0, 0F)
        }
    }


    fun drawTile(context: DrawContext, tickDelta: Float, activeBattlePokemon: ActiveClientBattlePokemon, left: Boolean, rank: Int) {
        val mc = MinecraftClient.getInstance()

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

        val hue = activeBattlePokemon.getHue()
        val r = ((hue shr 16) and 0b11111111) / 255F
        val g = ((hue shr 8) and 0b11111111) / 255F
        val b = (hue and 0b11111111) / 255F

        val truePokemon = activeBattlePokemon.actor.pokemon.find { it.uuid == activeBattlePokemon.battlePokemon?.uuid }

        drawBattleTile(
            context = context,
            x = x,
            y = y.toFloat(),
            partialTicks = tickDelta,
            reversed = !left,
            species = battlePokemon.species,
            level = battlePokemon.level,
            aspects = battlePokemon.aspects,
            displayName = battlePokemon.displayName,
            gender = battlePokemon.gender,
            status = battlePokemon.status,
            state = battlePokemon.state,
            colour = Triple(r, g, b),
            opacity = opacity.toFloat(),
            ballState = activeBattlePokemon.ballCapturing,
            maxHealth = battlePokemon.maxHp.toInt(),
            health = battlePokemon.hpValue,
            isFlatHealth = battlePokemon.isHpFlat
        )
    }

    fun drawBattleTile(
        context: DrawContext,
        x: Float,
        y: Float,
        partialTicks: Float,
        reversed: Boolean,
        species: Species,
        level: Int,
        aspects: Set<String>,
        displayName: MutableText,
        gender: Gender,
        status: PersistentStatus?,
        state: PoseableEntityState<PokemonEntity>?,
        colour: Triple<Float, Float, Float>?,
        opacity: Float,
        ballState: ClientBallDisplay? = null,
        maxHealth: Int,
        health: Float,
        isFlatHealth: Boolean
    ) {
        val portraitStartX = x + if (!reversed) PORTRAIT_OFFSET_X else { TILE_WIDTH - PORTRAIT_DIAMETER - PORTRAIT_OFFSET_X }
        val matrices = context.matrices
        blitk(
            matrixStack = matrices,
            texture = battleInfoUnderlay,
            y = y + PORTRAIT_OFFSET_Y,
            x = portraitStartX,
            height = PORTRAIT_DIAMETER,
            width = PORTRAIT_DIAMETER,
            alpha = opacity
        )

        // Second render the Pokémon through the scissors
        context.enableScissor(
            portraitStartX.toInt(),
            (y + PORTRAIT_OFFSET_Y).toInt(),
            (portraitStartX + PORTRAIT_DIAMETER).toInt(),
            (y + PORTRAIT_DIAMETER + PORTRAIT_OFFSET_Y).toInt(),
        )
        val matrixStack = MatrixStack()
        matrixStack.translate(
            portraitStartX + PORTRAIT_DIAMETER / 2.0,
            y.toDouble() + PORTRAIT_OFFSET_Y - 5.0 ,
            0.0
        )
        matrixStack.push()
        if (ballState != null && ballState.stateEmitter.get() == EmptyPokeBallEntity.CaptureState.SHAKE) {
            drawPokeBall(
                state = ballState,
                matrixStack = matrixStack,
                partialTicks = partialTicks
            )
        } else {
            matrixStack.push()
            drawPortraitPokemon(
                species = species,
                aspects = aspects,
                matrixStack = matrixStack,
                scale = 18F * (ballState?.scale ?: 1F),
                reversed = reversed,
                state = state,
                partialTicks = partialTicks
            )
            matrixStack.pop()
        }
        matrixStack.pop()
        context.disableScissor()

        // Third render the tile
        blitk(
            matrixStack = matrices,
            texture = if (reversed) battleInfoBaseFlipped else battleInfoBase,
            x = x,
            y = y,
            height = TILE_HEIGHT,
            width = TILE_WIDTH,
            alpha = opacity
        )

        if (colour != null) {
            val (r, g, b) = colour
            blitk(
                matrixStack = matrices,
                texture = if (reversed) battleInfoRoleFlipped else battleInfoRole,
                x = x + if (reversed) 93 else 11,
                y = y + 1,
                height = 3,
                width = 27,
                alpha = opacity,
                red = r,
                green = g,
                blue = b
            )
        }

        if (status != null) {
            val statusWidth = 37
            blitk(
                matrixStack = matrices,
                texture = cobblemonResource("textures/gui/battle/battle_status_" + status.showdownName + ".png"),
                x = x + if (reversed) 56 else 38,
                y = y + 28,
                height = 7,
                width = statusWidth,
                uOffset = if (reversed) 0 else statusWidth,
                textureWidth = statusWidth * 2,
                alpha = opacity
            )

            drawScaledText(
                context = context,
                font = CobblemonResources.DEFAULT_LARGE,
                text = lang("ui.status." + status.showdownName).bold(),
                x = x + if (reversed) 78 else 42,
                y = y + 27,
                opacity = opacity
            )
        }

        // Draw labels
        val infoBoxX = x + if (!reversed) PORTRAIT_DIAMETER + PORTRAIT_OFFSET_X + INFO_OFFSET_X else INFO_OFFSET_X
        drawScaledText(
            context = context,
            font = CobblemonResources.DEFAULT_LARGE,
            text = displayName.bold(),
            x = infoBoxX,
            y = y + 7,
            opacity = opacity,
            shadow = true
        )

        if (gender != Gender.GENDERLESS) {
            val isMale = gender == Gender.MALE
            val textSymbol = if (isMale) "♂".text().bold() else "♀".text().bold()
            drawScaledText(
                context = context,
                font = CobblemonResources.DEFAULT_LARGE,
                text = textSymbol,
                x = infoBoxX + 53,
                y = y + 7,
                colour = if (isMale) 0x32CBFF else 0xFC5454,
                opacity = opacity,
                shadow = true
            )
        }

        drawScaledText(
            context = context,
            font = CobblemonResources.DEFAULT_LARGE,
            text = lang("ui.lv").bold(),
            x = infoBoxX + 59,
            y = y + 7,
            opacity = opacity,
            shadow = true
        )

        drawScaledText(
            context = context,
            font = CobblemonResources.DEFAULT_LARGE,
            text = level.toString().text().bold(),
            x = infoBoxX + 72,
            y = y + 7,
            opacity = opacity,
            shadow = true
        )
        val hpRatio = if (isFlatHealth) health / maxHealth else health
        val (healthRed, healthGreen) = getDepletableRedGreen(hpRatio)
        val fullWidth = 83
        val barWidth = hpRatio * fullWidth
        val barX = if (!reversed) infoBoxX - 2 else infoBoxX + 3 + (fullWidth - barWidth)
        blitk(
            matrixStack = matrices,
            texture = CobblemonResources.WHITE,
            x = barX,
            y = y + 22,
            height = 4,
            width = barWidth,
            red = healthRed * 0.8F,
            green = healthGreen * 0.8F,
            blue = 0.27F
        )

        val text = if (isFlatHealth) {
            "${health.toInt()}/$maxHealth"
        } else {
            "${ceil(health * 100)}%"
        }.text()

        drawScaledText(
            context = context,
            text = text,
            x = infoBoxX + (if (!reversed) 39.5 else 44.5),
            y = y + 22,
            scale = 0.5F,
            opacity = opacity,
            centered = true,
            shadow = true
        )
    }

    private fun drawPokeBall(
        state: ClientBallDisplay,
        matrixStack: MatrixStack,
        scale: Float = 5F,
        partialTicks: Float,
        reversed: Boolean = false
    ) {
        val model = PokeBallModelRepository.getPoser(state.pokeBall.name, state.aspects)
        val texture = PokeBallModelRepository.getTexture(state.pokeBall.name, state.aspects, state.animationSeconds)
        val renderType = model.getLayer(texture)

        RenderSystem.applyModelViewMatrix()
        val quaternion1 = RotationAxis.POSITIVE_Y.rotationDegrees(-32F * if (reversed) -1F else 1F)
        val quaternion2 = RotationAxis.POSITIVE_X.rotationDegrees(5F)

        model.getPose(PoseType.PORTRAIT)?.let { state.setPose(it.poseName) }
        state.timeEnteredPose = 0F
        state.updatePartialTicks(partialTicks)
        model.setupAnimStateful(null, state, 0F, 0F, 0F, 0F, 0F)

        matrixStack.scale(scale, scale, -scale)
        matrixStack.translate(0.0, 5.5, -4.0)
        matrixStack.push()

        matrixStack.scale(scale * state.scale, scale * state.scale, 0.1F)

        matrixStack.multiply(quaternion1)
        matrixStack.multiply(quaternion2)

        val light1 = Vector3f(2.2F, 4.0F, -4.0F)
        val light2 = Vector3f(1.1F, -4.0F, 7.0F)
        RenderSystem.setShaderLights(light1, light2)
        quaternion1.conjugate()

        val immediate = MinecraftClient.getInstance().bufferBuilders.entityVertexConsumers
        val buffer = immediate.getBuffer(renderType)
        val packedLight = LightmapTextureManager.pack(11, 7)
        model.render(matrixStack, buffer, packedLight, OverlayTexture.DEFAULT_UV, 1F, 1F, 1F, 1F)

        immediate.draw()

        matrixStack.pop()

        DiffuseLighting.enableGuiDepthLighting()
    }

    fun onLogout() {
        this.opacity = MIN_OPACITY
        this.passedSeconds = 0F
        this.lastKnownBattle = null
    }

}