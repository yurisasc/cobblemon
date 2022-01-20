package com.cablemc.pokemoncobbled.client.gui

import com.cablemc.pokemoncobbled.client.CobbledResources
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
import net.minecraft.network.chat.TranslatableComponent
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

class PartyOverlay(minecraft: Minecraft = Minecraft.getInstance()) : Gui(minecraft) {
    var selectedSlot = 0
    val partyBase = cobbledResource("ui/party/party_base.png")
    val partySlot = cobbledResource("ui/party/party_slot.png")
    val underlay = cobbledResource("ui/party/party_slot_underlay.png")
    val expBar = cobbledResource("ui/party/party_overlay_exp.png")
    val hpBar = cobbledResource("ui/party/party_overlay_hp.png")

    @SubscribeEvent
    fun onRenderGameOverlay(event: RenderGameOverlayEvent.Pre) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) {
            return
        }

        val panelX = 4
        val party = PokemonCobbledClient.storage.myParty
        if (party.slots.none { it != null }) {
            return
        }

        val slotHeight = 36
        val portraitRadius = 28
        val baseExtra = 9
        val totalHeight = party.slots.size * slotHeight + baseExtra
        val ratio = 384 / 270F
        val midY = minecraft.window.guiScaledHeight / 2
        val startY = midY - totalHeight / 2
        val originalFrameOffsetX = 14
        val originalFrameOffsetY = 10

        party.forEachIndexed { index, pokemon ->

            blitk(
                poseStack = event.matrixStack,
                texture = underlay,
                x = panelX + originalFrameOffsetX,
                y = startY + slotHeight * index + originalFrameOffsetY,
                height = portraitRadius,
                width = portraitRadius
            )

            if (pokemon != null) {
                val scaleIt: (Int) -> Int = { (it * minecraft.window.guiScale).toInt() }
                val y = startY + slotHeight * index + originalFrameOffsetY

                val unscaledStartY = minecraft.window.height / 2 + scaleIt(party.slots.size * 22) / 2 + scaleIt(baseExtra)

                RenderSystem.enableScissor(
                    scaleIt(panelX + originalFrameOffsetX),
                    unscaledStartY - scaleIt(slotHeight) * index,
                    scaleIt(portraitRadius),
                    scaleIt(portraitRadius)
                )

                val poseStack = PoseStack()
                poseStack.translate(
                    (panelX + originalFrameOffsetX).toDouble() + portraitRadius / 2.0,
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
                event.matrixStack.pushPose()
                val fontScale = 1F
                val horizontalScale = fontScale * 1F
                event.matrixStack.scale(horizontalScale, fontScale, 1F)
                minecraft.font.draw(
                    event.matrixStack,
                    TranslatableComponent("Crabominable"/*"pokemon.species.name"*/).also {
                        it.style = it.style.withFont(CobbledResources.shulVokalFontSmall)
                    },
                    (panelX + 14F) / horizontalScale,
                    (startY + slotHeight * index + slotHeight * 0.84F + 9.5F) / fontScale,
                    0xFFFFFF
                )
                event.matrixStack.popPose()
            }
        }

        minecraft.font.draw(
            event.matrixStack,
            TranslatableComponent("Pokémon Team").also {
                it.style = it.style.withFont(CobbledResources.shulVokalFontSmall)
            },
            panelX + 3F,
            startY + 5F,
            0xFFFFFF
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