package com.cablemc.pokemoncobbled.common.client.gui

import com.cablemc.pokemoncobbled.common.api.gui.blitk
import com.cablemc.pokemoncobbled.common.api.gui.drawRectangle
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.keybind.keybinds.HidePartyBinding
import com.cablemc.pokemoncobbled.common.client.render.drawScaled
import com.cablemc.pokemoncobbled.common.client.render.getDepletableRedGreen
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.repository.PokemonModelRepository
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.asTranslated
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.platform.Lighting
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Vector3f
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.screens.ChatScreen
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.network.chat.TranslatableComponent
import kotlin.math.roundToInt

class PartyOverlay(minecraft: Minecraft = Minecraft.getInstance()) : Gui(minecraft) {
    val partySlot = cobbledResource("ui/party/party_slot.png")
    val underlay = cobbledResource("ui/party/party_slot_underlay.png")
    val underlaySelected = cobbledResource("ui/party/party_slot_underlay_selected.png")
    val expBar = cobbledResource("ui/party/party_overlay_exp.png")
    val hpBar = cobbledResource("ui/party/party_overlay_hp.png")
    val ring = cobbledResource("ui/party/ring.png")
    val ring2 = cobbledResource("ui/party/ring2.png")
    val screenExemptions: List<Class<out Screen>> = listOf(
        ChatScreen::class.java
    )

    fun onRenderGameOverlay(poseStack: PoseStack, partialDeltaTicks: Float) {
        val minecraft = Minecraft.getInstance()
        // Hiding if a Screen is open and not exempt
        if (minecraft.screen != null) {
            if (!screenExemptions.contains(minecraft.screen?.javaClass as Class<out Screen>))
                return
        }
        // Hiding if toggled via Keybind
        if (HidePartyBinding.shouldHide)
            return

        val panelX = 2
        val party = PokemonCobbledClient.storage.myParty
        if (party.slots.none { it != null }) {
            return
        }

        val slotHeight = 32
        val portraitRadius = 24
        val totalHeight = party.slots.size * slotHeight
        val ratio = 324 / 252F
        val midY = minecraft.window.guiScaledHeight / 2
        val startY = midY - totalHeight / 2
        val frameOffsetX = 8.5
        val frameOffsetY = 1

        val scaleIt: (Int) -> Int = { (it * minecraft.window.guiScale).toInt() }
        val downscaleIt: (Number) -> Int = { (it.toFloat() / 4F * minecraft.window.guiScale).roundToInt() }

        party.forEachIndexed { index, pokemon ->
            if (index != 0) {
                return@forEachIndexed
            }
//            blitk(
//                poseStack = poseStack,
//                texture = if (PokemonCobbledClient.storage.selectedSlot == index) underlaySelected else underlay,
//                x = panelX + frameOffsetX,
//                y = startY + slotHeight * index + frameOffsetY,
//                height = portraitRadius,
//                width = portraitRadius
//            )

            if (pokemon != null) {
                val y = startY + slotHeight * index + frameOffsetY

                val height = minecraft.window.height
                val scaledTotalHeight = downscaleIt(totalHeight)


//                RenderSystem.enableScissor(
//                    ((panelX + frameOffsetX) * minecraft.window.guiScale).roundToInt(),
//                    height / 2 + scaledTotalHeight * 2 - scaleIt(slotHeight * (index + 1)),// - scaleIt(slotHeight) * index,
//                    (portraitRadius * minecraft.window.guiScale).roundToInt(),
//                    (portraitRadius * minecraft.window.guiScale).roundToInt()
//                )


                val poseStack = PoseStack()
                poseStack.translate(
                    panelX + frameOffsetX + portraitRadius / 2.0,
                    y.toDouble(),
                    0.0
                )
                poseStack.scale(1F, 1F, 1F)

                RenderSystem.disableBlend()
                RenderSystem.enableBlend()
                RenderSystem.blendFunc(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE)
//                drawPokemon(pokemon, poseStack)

                blitk(
                    poseStack = PoseStack(),
                    texture = partySlot,
                    x = 0,
                    y = 0,
                    width = 1000,
                    height = 1000,
                    blend = false
                )

                RenderSystem.blendFuncSeparate(
                    GlStateManager.SourceFactor.ZERO,
                    GlStateManager.DestFactor.DST_COLOR,
                    GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ZERO
                )
                blitk(
                    poseStack = PoseStack(),
                    texture = ring2,
                    x = panelX + frameOffsetX,
                    y = y,
                    width = portraitRadius,
                    height = portraitRadius,
                    blend = false
                )

//                val x = panelX + frameOffsetX
//                val width = portraitRadius
//                val height2 = portraitRadius
//                val textureWidth = width
//                val textureHeight = height
//                RenderSystem.setShader { GameRenderer.getPositionTexShader() }
//                ring2.run { RenderSystem.setShaderTexture(0, this) }
//                RenderSystem.setShaderColor(1F, 1F, 1F, 1F)
//                drawRectangle(
//                    poseStack.last().pose(),
//                    x.toFloat(), y.toFloat(), x.toFloat() + width.toFloat(), y.toFloat() + height2.toFloat(),
//                    blitOffset.toFloat(),
//                    0.toFloat() / textureWidth.toFloat(), (0.toFloat() + width.toFloat()) / textureWidth.toFloat(),
//                    0.toFloat() / textureHeight.toFloat(), (0.toFloat() + height.toFloat()) / textureHeight.toFloat()
//                )


                RenderSystem.disableBlend()
//                RenderSystem.disableScissor()
            }
        }

        party.slots.forEachIndexed { index, pokemon ->
//            blitk(
//                poseStack = poseStack,
//                texture = partySlot,
//                x = panelX,
//                y = startY + slotHeight * index,
//                height = slotHeight,
//                width = ratio * slotHeight
//            )

            if (pokemon != null) {
                val hpRatio = pokemon.currentHealth / pokemon.hp.toFloat()
                val barHeightMax = 22F
                val hpBarHeight = hpRatio * barHeightMax
                val expRatio = 1.0
                val expBarHeight = expRatio * barHeightMax
                val hpWidthToHeight = 72 / 174F
                val expWidthToHeight = 45 / 174F

                val (r, g) = getDepletableRedGreen(hpRatio)
                val b = 0

                blitk(
                    poseStack = poseStack,
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
                    poseStack = poseStack,
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

                minecraft.font.drawScaled(
                    poseStack = poseStack,
                    text = pokemon.species.translatedName,
                    x = panelX + 2.5F,
                    y = startY + slotHeight * index + slotHeight * 0.84F - 1F,
                    scaleX = fontScale,
                    scaleY = horizontalScale
                )

                minecraft.font.drawScaled(
                    poseStack = poseStack,
                    text = "pokemoncobbled.ui.lv".asTranslated(),
                    x = panelX + 2.5F,
                    y = startY + slotHeight * index + slotHeight * 0.84F - 10.75F,
                    scaleX = 0.4F,
                    scaleY = 0.4F
                )

                val width = minecraft.font.width(100.toString())
                minecraft.font.drawScaled(
                    poseStack = poseStack,
                    text = TranslatableComponent(100.toString()),
                    x = panelX + 6.5F - width / 4F,
                    y = startY + slotHeight * index + slotHeight * 0.84F - 7F,
                    scaleX = 0.45F,
                    scaleY = 0.45F
                )
            }
        }
    }

    fun drawPokemon(pokemon: Pokemon, poseStack: PoseStack) {
        val model = PokemonModelRepository.getModel(pokemon).entityModel
        val texture = PokemonModelRepository.getModelTexture(pokemon)

        val renderType = model.renderType(texture)

        val scale = 13F
        RenderSystem.applyModelViewMatrix()
        poseStack.scale(scale, scale, -scale)
        val quaternion1 = Vector3f.YP.rotationDegrees(-32F)

        if (model is PokemonPoseableModel) {
            model.setupAnimStateless(PoseType.NONE)
            poseStack.translate(model.portraitTranslation.x, model.portraitTranslation.y, model.portraitTranslation.z)
            poseStack.scale(model.portraitScale, model.portraitScale, 0.01F)
        }

        poseStack.mulPose(quaternion1)
        Lighting.setupForEntityInInventory()
        val entityRenderDispatcher = Minecraft.getInstance().entityRenderDispatcher
        quaternion1.conj()
        entityRenderDispatcher.overrideCameraOrientation(quaternion1)
        entityRenderDispatcher.setRenderShadow(false)

        val packedLight = LightTexture.pack(15, 15)

        val bufferSource = Minecraft.getInstance().renderBuffers().bufferSource()

        val buffer = bufferSource.getBuffer(renderType)
        model.renderToBuffer(poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F)
        bufferSource.endBatch()

        entityRenderDispatcher.setRenderShadow(true)
        Lighting.setupFor3DItems()
    }
}