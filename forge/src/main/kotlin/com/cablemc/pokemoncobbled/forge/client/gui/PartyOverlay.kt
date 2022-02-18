package com.cablemc.pokemoncobbled.forge.client.gui

import com.cablemc.pokemoncobbled.forge.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.forge.client.keybinding.HidePartyBinding
import com.cablemc.pokemoncobbled.common.client.render.drawScaled
import com.cablemc.pokemoncobbled.common.client.render.getDepletableRedGreen
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.repository.PokemonModelRepository
import com.cablemc.pokemoncobbled.common.api.pokemon.stats.Stats
import com.cablemc.pokemoncobbled.common.entity.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.asTranslated
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.mojang.blaze3d.platform.Lighting
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Vector3f
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.screens.ChatScreen
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.network.chat.TranslatableComponent
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import kotlin.math.roundToInt

class PartyOverlay(minecraft: Minecraft = Minecraft.getInstance()) : Gui(minecraft) {
    val partyBase = cobbledResource("ui/party/party_base.png")
    val partySlot = cobbledResource("ui/party/party_slot.png")
    val underlay = cobbledResource("ui/party/party_slot_underlay.png")
    val underlaySelected = cobbledResource("ui/party/party_slot_underlay_selected.png")
    val expBar = cobbledResource("ui/party/party_overlay_exp.png")
    val hpBar = cobbledResource("ui/party/party_overlay_hp.png")
    val screenExemptions: List<Class<out Screen>> = listOf(
        ChatScreen::class.java
    )

    @SubscribeEvent
    fun onRenderGameOverlay(event: RenderGameOverlayEvent.Pre) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) {
            return
        }
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

        val slotHeight = 36
        val portraitRadius = 28
        val baseExtra = 10
        val totalHeight = party.slots.size * slotHeight + baseExtra
        val ratio = 384 / 270F
        val midY = minecraft.window.guiScaledHeight / 2
        val startY = midY - totalHeight / 2
        val frameOffsetX = 14
        val frameOffsetY = 10

        val scaleIt: (Int) -> Int = { (it * minecraft.window.guiScale).toInt() }
        val downscaleIt: (Number) -> Int = { (it.toFloat() / 4F * minecraft.window.guiScale).roundToInt() }

        party.forEachIndexed { index, pokemon ->
            blitk(
                poseStack = event.matrixStack,
                texture = if (PokemonCobbledClient.storage.selectedSlot == index) underlaySelected else underlay,
                x = panelX + frameOffsetX,
                y = startY + slotHeight * index + frameOffsetY,
                height = portraitRadius,
                width = portraitRadius
            )

            if (pokemon != null) {
                val y = startY + slotHeight * index + frameOffsetY

                val height = minecraft.window.height
                val scaledTotalHeight = downscaleIt(totalHeight)

                RenderSystem.enableScissor(
                    ((panelX + frameOffsetX) * minecraft.window.guiScale).roundToInt(),
                    height / 2 + scaledTotalHeight * 2 - downscaleIt(baseExtra) - scaleIt(slotHeight * (index + 1)),// - scaleIt(slotHeight) * index,
                    (portraitRadius * minecraft.window.guiScale).roundToInt(),
                    (portraitRadius * minecraft.window.guiScale).roundToInt()
                )

                val poseStack = PoseStack()
                poseStack.translate(
                    (panelX + frameOffsetX).toDouble() + portraitRadius / 2.0,
                    y.toDouble(),
                    0.0
                )
                poseStack.scale(1F, 1F, 1F)
                drawPokemon(pokemon, poseStack)

                RenderSystem.disableScissor()
            }
        }

        party.slots.forEachIndexed { index, pokemon ->
            if (index == 0) {
                blitk(
                    poseStack = event.matrixStack,
                    texture = partyBase,
                    x = panelX,
                    y = startY,
                    height = slotHeight + baseExtra,
                    width = ratio * slotHeight
                )
            } else {
                blitk(
                    poseStack = event.matrixStack,
                    texture = partySlot,
                    x = panelX,
                    y = startY + slotHeight * index + baseExtra,
                    height = slotHeight,
                    width = ratio * slotHeight
                )
            }

            if (pokemon != null) {
                val hpRatio = pokemon.health / (pokemon.stats[Stats.HP] ?: pokemon.health).toFloat()
                val barHeightMax = 25.25F
                val hpBarHeight = hpRatio * barHeightMax
                val expRatio = 1.0
                val expBarHeight = expRatio * barHeightMax
                val hpWidthToHeight = 75 / 188F
                val expWidthToHeight = 49 / 188F

                val (r, g) = getDepletableRedGreen(hpRatio)
                val b = 0

                blitk(
                    poseStack = event.matrixStack,
                    texture = hpBar,
                    x = panelX + 37F,
                    y = startY + slotHeight * index + baseExtra + 1.5F + (barHeightMax - hpBarHeight) + if (index == 0) 0F else 0.25F,
                    width = hpWidthToHeight * barHeightMax,
                    height = hpBarHeight,
                    textureHeight = hpBarHeight / hpRatio,
                    vOffset = barHeightMax - hpBarHeight,
                    red = r,
                    green = g,
                    blue = b
                )

                blitk(
                    poseStack = event.matrixStack,
                    texture = expBar,
                    x = panelX + 42.5F,
                    y = startY + slotHeight * index + baseExtra + 1.5F + (barHeightMax - expBarHeight) + if (index == 0) 0F else 0.25F,
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
                    poseStack = event.matrixStack,
                    text = pokemon.species.translatedName.asTranslated(),
                    x = panelX + 15F,
                    y = startY + slotHeight * index + slotHeight * 0.84F + baseExtra - 0.5F,
                    scaleX = fontScale,
                    scaleY = horizontalScale
                )

                minecraft.font.drawScaled(
                    poseStack = event.matrixStack,
                    text = "pokemoncobbled.ui.lv".asTranslated(),
                    x = panelX + 2.75F,
                    y = startY + slotHeight * index + slotHeight * 0.84F + baseExtra - 4.5F,
                    scaleX = 0.4F,
                    scaleY = 0.4F
                )

                val width = minecraft.font.width(pokemon.level.toString())
                minecraft.font.drawScaled(
                    poseStack = event.matrixStack,
                    text = TranslatableComponent(pokemon.level.toString()),
                    x = panelX + 7F - width / 4F,
                    y = startY + slotHeight * index + slotHeight * 0.84F + baseExtra - 0.5F,
                    scaleX = 0.5F,
                    scaleY = 0.5F
                )
            }
        }

        val headingFontScale = 0.6F
        minecraft.font.drawScaled(
            poseStack = event.matrixStack,
            text = "pokemoncobbled.ui.pokemonteam".asTranslated(),
            x = panelX + 3F,
            y = startY + 2.5F,
            scaleX = headingFontScale,
            scaleY = headingFontScale
        )
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
            poseStack.scale(model.portraitScale, model.portraitScale, model.portraitScale)
        }

        poseStack.mulPose(quaternion1)
        Lighting.setupForEntityInInventory()
        val entityRenderDispatcher = Minecraft.getInstance().entityRenderDispatcher
        quaternion1.conj()
        entityRenderDispatcher.overrideCameraOrientation(quaternion1)
        entityRenderDispatcher.setRenderShadow(false)

        val bufferSource = Minecraft.getInstance().renderBuffers().bufferSource()
        val buffer = bufferSource.getBuffer(renderType)

        val packedLight = LightTexture.pack(15, 15)
        model.renderToBuffer(poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F)
        bufferSource.endBatch()
        entityRenderDispatcher.setRenderShadow(true)
        Lighting.setupFor3DItems()
    }
}