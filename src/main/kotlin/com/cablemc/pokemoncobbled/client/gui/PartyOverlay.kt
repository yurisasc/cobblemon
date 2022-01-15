package com.cablemc.pokemoncobbled.client.gui

import com.cablemc.pokemoncobbled.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.client.render.models.blockbench.repository.PokemonModelRepository
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.mojang.blaze3d.platform.Lighting
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Vector3f
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import kotlin.math.roundToInt

class PartyOverlay(
    minecraft: Minecraft = Minecraft.getInstance()
) : Gui(minecraft) {
    var selectedSlot = 0
//    val partyBase = cobbledResource("ui/party/party_base.png")
    val partySlot = cobbledResource("ui/party/party_slot_new.png")
    val underlay = cobbledResource("ui/party/party_slot_underlay.png")
    val selectedResource = cobbledResource("party/selected_slot.png")

    @SubscribeEvent
    fun onRenderGameOverlay(event: RenderGameOverlayEvent.Pre) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) {
            return
        }

        val panelX = 5
        val party = PokemonCobbledClient.storage.myParty
        if (party.slots.none { it != null }) {
            return
        }

        val slotHeight = 38
        val totalHeight = party.slots.size * slotHeight
        val ratio = 388/270F
        val midY = minecraft.window.guiScaledHeight / 2
        val startY = midY - totalHeight / 2
        val originalFrameOffsetX = 80
        val originalFrameOffsetY = 10
        val originalHeightToUsedHeight = slotHeight / 309F
        val frameWidth = 192 * originalHeightToUsedHeight
        val downscaledFrameOffsetX = originalFrameOffsetX * ratio
        val downscaledFrameOffsetY = originalFrameOffsetY * ratio

        party.forEachIndexed { index, pokemon ->
            if (pokemon != null) {
                val scaleIt: (Int) -> Int = { (it * minecraft.window.guiScale).toInt() }
                val y = minecraft.window.guiScaledHeight - (startY + slotHeight * (index + 1) + 1) + if (index == 0) 0 else 1

                RenderSystem.viewport(
                    scaleIt(panelX),
                    scaleIt((y + downscaledFrameOffsetY).roundToInt() - 4),
                    scaleIt(frameWidth.toInt() + 2 + (downscaledFrameOffsetX * originalHeightToUsedHeight).roundToInt()),
                    scaleIt(frameWidth.toInt() + 2 + (downscaledFrameOffsetY * originalHeightToUsedHeight).roundToInt())
                )
//
                drawPokemon(pokemon)
            }
        }

        RenderSystem.viewport(0, 0, minecraft.window.width, minecraft.window.height)

        party.slots.forEachIndexed { index, pokemon ->
            blitk(
                poseStack = event.matrixStack,
                texture = underlay,
                x = panelX,
                y = startY + slotHeight * index,
                height = slotHeight,
                width = ratio * slotHeight
            )

//            if (index == 0) {
                blitk(
                    poseStack = event.matrixStack,
                    texture = partySlot,
                    x = panelX,
                    y = startY + slotHeight * index,
                    height = slotHeight,
                    width = ratio * slotHeight
                )

            if (pokemon != null) {

            }
//            } else {
//                blitk(
//                    poseStack = event.matrixStack,
//                    texture = partySlot,
//                    x = panelX,
//                    y = startY + slotHeight * index - 2,
//                    height = slotHeight + 2,
//                    width = ratio * slotHeight
//                )
//            }
        }
    }

    fun drawPokemon(pokemon: Pokemon) {
        val model = PokemonModelRepository.getModel(pokemon).entityModel
        val texture = PokemonModelRepository.getModelTexture(pokemon)

        val renderType = model.renderType(texture)

        val scale = 1200F / minecraft.window.guiScale.toFloat()
        RenderSystem.applyModelViewMatrix()
        val posestack1 = PoseStack()
        posestack1.translate(minecraft.window.guiScaledWidth / 2.0, 0.0, -100.0)
        posestack1.scale(scale, scale * 0.75F, -scale * 0.1F)
        val quaternion1 = Vector3f.YP.rotationDegrees(-32F)

        if (model is PokemonPoseableModel) {
            model.setupAnimStateless(PoseType.NONE)
            posestack1.translate(model.portraitTranslation.x, model.portraitTranslation.y, model.portraitTranslation.z)
            posestack1.scale(model.portraitScale, model.portraitScale, model.portraitScale)
        }

        posestack1.mulPose(quaternion1)
        Lighting.setupForEntityInInventory()
        val entityRenderDispatcher = Minecraft.getInstance().entityRenderDispatcher
        quaternion1.conj()
        entityRenderDispatcher.overrideCameraOrientation(quaternion1)
        entityRenderDispatcher.setRenderShadow(false)

        val bufferSource = Minecraft.getInstance().renderBuffers().bufferSource()
        val buffer = bufferSource.getBuffer(renderType)

        val packedLight = LightTexture.pack(15, 15)
        model.renderToBuffer(posestack1, buffer, packedLight, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F)
        bufferSource.endBatch()
        entityRenderDispatcher.setRenderShadow(true)
        RenderSystem.applyModelViewMatrix()
        Lighting.setupFor3DItems()
    }
}