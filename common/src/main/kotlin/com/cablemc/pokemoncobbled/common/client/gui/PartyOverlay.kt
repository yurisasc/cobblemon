package com.cablemc.pokemoncobbled.common.client.gui

import com.cablemc.pokemoncobbled.common.api.gui.blitk
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.gui.battle.BattleGUI
import com.cablemc.pokemoncobbled.common.client.keybind.keybinds.HidePartyBinding
import com.cablemc.pokemoncobbled.common.client.render.drawScaled
import com.cablemc.pokemoncobbled.common.client.render.getDepletableRedGreen
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.repository.PokemonModelRepository
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.asTranslated
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.hud.InGameHud
import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.render.DiffuseLighting
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.TranslatableText
import net.minecraft.util.math.Vec3f
import kotlin.math.roundToInt

class PartyOverlay : InGameHud(MinecraftClient.getInstance()) {

    val partySlot = cobbledResource("ui/party/party_slot.png")
    val underlay = cobbledResource("ui/party/party_slot_underlay.png")
    val underlaySelected = cobbledResource("ui/party/party_slot_underlay_selected.png")
    val expBar = cobbledResource("ui/party/party_overlay_exp.png")
    val hpBar = cobbledResource("ui/party/party_overlay_hp.png")
    val screenExemptions: List<Class<out Screen>> = listOf(
        ChatScreen::class.java,
        BattleGUI::class.java
    )

    override fun render(matrixStack: MatrixStack, partialDeltaTicks: Float) {
        val minecraft = MinecraftClient.getInstance()
        val player = minecraft.player

        // Hiding if a Screen is open and not exempt
        if (minecraft.currentScreen != null) {
            if (!screenExemptions.contains(minecraft.currentScreen?.javaClass as Class<out Screen>))
                return
        }
        if (minecraft.options.debugEnabled) {
            return
        }
        // Hiding if toggled via Keybind
        if (HidePartyBinding.shouldHide)
            return

        val panelX = 0
        val party = PokemonCobbledClient.storage.myParty
        if (party.slots.none { it != null }) {
            return
        }

        val slotHeight = 32
        val portraitRadius = 23
        val totalHeight = party.slots.size * slotHeight
        val ratio = 324 / 252F
        val midY = minecraft.window.scaledHeight / 2
        val startY = midY - totalHeight / 2
        val frameOffsetX = 8.5
        val frameOffsetY = 1

        val scaleIt: (Int) -> Int = { (it * minecraft.window.scaleFactor).toInt() }
        val downscaleIt: (Number) -> Int = { (it.toFloat() / 4F * minecraft.window.scaleFactor).roundToInt() }

        party.forEachIndexed { index, pokemon ->
            blitk(
                matrixStack = matrixStack,
                texture = if (PokemonCobbledClient.storage.selectedSlot == index) underlaySelected else underlay,
                x = panelX + frameOffsetX - 1,
                y = startY + slotHeight * index + frameOffsetY - 1,
                height = portraitRadius + 2,
                width = portraitRadius + 2
            )

            if (pokemon != null) {
                val y = startY + slotHeight * index + frameOffsetY

                val height = minecraft.window.height
                val scaledTotalHeight = downscaleIt(totalHeight)

                RenderSystem.enableScissor(
                    ((panelX + frameOffsetX) * minecraft.window.scaleFactor).roundToInt(),
                    height / 2 + scaledTotalHeight * 2 + scaleIt(8) - scaleIt(slotHeight * (index + 1)),
                    (portraitRadius * minecraft.window.scaleFactor).roundToInt(),
                    (portraitRadius * minecraft.window.scaleFactor).roundToInt()
                )


                val matrixStack = MatrixStack()
                matrixStack.translate(
                    panelX + frameOffsetX + portraitRadius / 2.0,
                    y.toDouble(),
                    0.0
                )
                matrixStack.scale(1F, 1F, 1F)

                drawPokemon(pokemon, matrixStack)

                RenderSystem.disableScissor()
            }
        }

        // Some long models end up translated such that the text ends up behind the invisible viewport rendered bits.
        // Kinda messed up but pushing these next elements forward seems a cheap enough fix.
        matrixStack.translate(0.0, 0.0, 10.0)
        party.slots.forEachIndexed { index, pokemon ->
            blitk(
                matrixStack = matrixStack,
                texture = partySlot,
                x = panelX,
                y = startY + slotHeight * index,
                height = slotHeight,
                width = ratio * slotHeight
            )

            if (pokemon != null) {
                val hpRatio = pokemon.currentHealth / pokemon.hp.toFloat()
                val barHeightMax = 22F
                val hpBarHeight = hpRatio * barHeightMax
                val expForThisLevel = pokemon.experience - if (pokemon.level == 1) 0 else pokemon.experienceGroup.getExperience(pokemon.level)
                val expToNextLevel = pokemon.experienceGroup.getExperience(pokemon.level + 1) - pokemon.experienceGroup.getExperience(pokemon.level)
                val expRatio = expForThisLevel / expToNextLevel.toFloat()

                val expBarHeight = expRatio * barHeightMax
                val hpWidthToHeight = 72 / 174F
                val expWidthToHeight = 45 / 174F

                val (r, g) = getDepletableRedGreen(hpRatio)
                val b = 0

                blitk(
                    matrixStack = matrixStack,
                    texture = hpBar,
                    x = panelX + 28.25F,
                    y = startY + slotHeight * index + 1.5F + (barHeightMax - hpBarHeight),
                    width = hpWidthToHeight * barHeightMax,
                    height = hpBarHeight,
                    textureHeight = hpBarHeight / hpRatio,
                    vOffset = barHeightMax - hpBarHeight,
                    red = r,
                    green = g,
                    blue = b
                )

                blitk(
                    matrixStack = matrixStack,
                    texture = expBar,
                    x = panelX + 33.5F,
                    y = startY + slotHeight * index + 1.5F + (barHeightMax - expBarHeight),
                    width = expWidthToHeight * barHeightMax,
                    height = expBarHeight,
                    textureHeight = expBarHeight / expRatio,
                    vOffset = barHeightMax - expBarHeight,
                    red = 0,
                    green = 0.784,
                    blue = 1.0
                )

                val fontScale = 0.5F
                val horizontalScale = fontScale * 1F

                minecraft.textRenderer.drawScaled(
                    matrixStack = matrixStack,
                    text = pokemon.species.translatedName,
                    x = panelX + 2.5F,
                    y = startY + slotHeight * index + slotHeight * 0.84F - 1F,
                    scaleX = fontScale,
                    scaleY = horizontalScale
                )

                minecraft.textRenderer.drawScaled(
                    matrixStack = matrixStack,
                    text = "pokemoncobbled.ui.lv".asTranslated(),
                    x = panelX + 2.5F,
                    y = startY + slotHeight * index + slotHeight * 0.84F - 10.75F,
                    scaleX = 0.4F,
                    scaleY = 0.4F
                )

                val width = minecraft.textRenderer.getWidth(pokemon.level.toString())
                minecraft.textRenderer.drawScaled(
                    matrixStack = matrixStack,
                    text = TranslatableText(pokemon.level.toString()),
                    x = panelX + 6.5F - width / 4F,
                    y = startY + slotHeight * index + slotHeight * 0.84F - 7F,
                    scaleX = 0.45F,
                    scaleY = 0.45F
                )

                val stateIcon = pokemon.state.getIcon(pokemon)
                if (stateIcon != null) {
                    blitk(
                        matrixStack = matrixStack,
                        texture = stateIcon,
                        x = panelX + 1.2F,
                        y = startY + slotHeight * index + frameOffsetY + 8,
                        height = 30 * 0.2,
                        width = 34 * 0.2
                    )
                }
            }
        }
    }

    fun drawPokemon(pokemon: Pokemon, matrixStack: MatrixStack) {
        val model = PokemonModelRepository.getEntityModel(pokemon.species, pokemon.aspects)
        val texture = PokemonModelRepository.getModelTexture(pokemon.species, pokemon.aspects)

        val renderType = model.getLayer(texture)

        val scale = 13F
        RenderSystem.applyModelViewMatrix()
        matrixStack.scale(scale, scale, -scale)
        val quaternion1 = Vec3f.POSITIVE_Y.getDegreesQuaternion(-32F)
        val quaternion2 = Vec3f.POSITIVE_X.getDegreesQuaternion(5F)

        model.setupAnimStateless(setOf(PoseType.PORTRAIT, PoseType.PROFILE))
        matrixStack.translate(model.portraitTranslation.x, model.portraitTranslation.y, model.portraitTranslation.z - 4)
        matrixStack.scale(model.portraitScale, model.portraitScale, 0.01F)

        matrixStack.multiply(quaternion1)
        matrixStack.multiply(quaternion2)

        val light1 = Vec3f(0.2F, 1.0F, -1.0F)
        val light2 = Vec3f(0.1F, -1.0F, 2.0F)
        RenderSystem.setShaderLights(light1, light2)
        quaternion1.conjugate()

        val immediate = MinecraftClient.getInstance().bufferBuilders.entityVertexConsumers
        val buffer = immediate.getBuffer(renderType)
        val packedLight = LightmapTextureManager.pack(8, 4)
        model.render(matrixStack, buffer, packedLight, OverlayTexture.DEFAULT_UV, 1F, 1F, 1F, 1F)

        immediate.draw()

        DiffuseLighting.enableGuiDepthLighting()
    }
}