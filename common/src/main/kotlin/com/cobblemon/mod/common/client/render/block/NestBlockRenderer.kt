/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.block

import com.cobblemon.mod.common.api.pokemon.breeding.Egg
import com.cobblemon.mod.common.api.pokemon.breeding.EggPatterns
import com.cobblemon.mod.common.block.entity.NestBlockEntity
import com.cobblemon.mod.common.client.render.atlas.CobblemonAtlases
import com.cobblemon.mod.common.client.render.layer.CobblemonRenderLayers
import com.cobblemon.mod.common.client.render.models.blockbench.repository.EggModelRepo
import com.cobblemon.mod.common.client.render.models.blockbench.setRotation
import com.cobblemon.mod.common.util.math.geometry.Axis
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.VertexBuffer
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Vec3d
import java.awt.Color

class NestBlockRenderer(private val context: BlockEntityRendererFactory.Context) : BlockEntityRenderer<NestBlockEntity> {
    override fun getRenderDistance(): Int {
        return MinecraftClient.getInstance().options.viewDistance.value * 16
    }
    override fun render(
        entity: NestBlockEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        if (entity.renderState == null) {
            entity.renderState = BasicBlockEntityRenderState()
        }
        val renderState = entity.renderState as BasicBlockEntityRenderState
        if (renderState.needsRebuild || renderState.vboLightLevel != light || renderState.vbo.vertexFormat == null) {
            renderToBuffer(entity, light, overlay, renderState.vbo, entity.egg)
            renderState.vboLightLevel = light
            renderState.needsRebuild = false
        }
        if (entity.egg != null) {
            matrices.push()
            CobblemonRenderLayers.EGG_LAYER.startDrawing()
            renderState.vbo.bind()
            renderState.vbo.draw(
                matrices.peek().positionMatrix.mul(RenderSystem.getModelViewMatrix()),
                RenderSystem.getProjectionMatrix(),
                GameRenderer.getRenderTypeCutoutProgram()
            )
            VertexBuffer.unbind()
            CobblemonRenderLayers.EGG_LAYER.endDrawing()
            matrices.pop()
        }

    }

    fun renderToBuffer(entity: NestBlockEntity, light: Int, overlay: Int, buffer: VertexBuffer, egg: Egg?) {
        if (egg != null) {
            val bufferBuilder = Tessellator.getInstance().buffer
            bufferBuilder.begin(
                CobblemonRenderLayers.EGG_LAYER.drawMode,
                CobblemonRenderLayers.EGG_LAYER.vertexFormat
            )
            val pattern = EggPatterns.patternMap[egg.patternId]!!
            val model = EggModelRepo.eggModels[pattern.model]
            val baseTexture = pattern.baseTexturePath
            val baseAtlasedTexture = CobblemonAtlases.EGG_PATTERN_ATLAS.getSprite(baseTexture)

            val primaryColor = Color.decode("#${egg.baseColor}")

            //Patching uvs so we can use atlases
            val baseModel = model?.createWithUvOverride(
                false,
                baseAtlasedTexture.x,
                baseAtlasedTexture.y,
                CobblemonAtlases.EGG_PATTERN_ATLAS.atlas.width,
                CobblemonAtlases.EGG_PATTERN_ATLAS.atlas.height
            )?.createModel()
            baseModel?.setRotation(Axis.Z_AXIS.ordinal, Math.toRadians(180.0).toFloat())
            val baseTextureModel = model?.createWithUvOverride(
                false,
                baseAtlasedTexture.x,
                baseAtlasedTexture.y,
                CobblemonAtlases.EGG_PATTERN_ATLAS.atlas.width,
                CobblemonAtlases.EGG_PATTERN_ATLAS.atlas.height
            )?.createModel()
            baseTextureModel?.setRotation(Axis.Z_AXIS.ordinal, Math.toRadians(180.0).toFloat())
            val matrixStack = MatrixStack()
            matrixStack.loadIdentity()
            //matrixStack.translate(0F, 0.3F, 0F)
            baseModel?.render(matrixStack, bufferBuilder, light, overlay)
            baseTextureModel?.render(
                matrixStack,
                bufferBuilder,
                light,
                overlay,
                primaryColor.red.toFloat() / 255F,
                primaryColor.green.toFloat() / 255F,
                primaryColor.blue.toFloat() / 255F,
                1.0F
            )


            pattern.overlayTexturePath?.let {
                val overlayAtlasedTexture = CobblemonAtlases.EGG_PATTERN_ATLAS.getSprite(it)
                val overlayColor = Color.decode("#${egg.overlayColor}")
                if (overlayColor != null) {
                    val overlayTextureModel = model?.createWithUvOverride(
                        false,
                        overlayAtlasedTexture.x,
                        overlayAtlasedTexture.y,
                        CobblemonAtlases.EGG_PATTERN_ATLAS.atlas.width,
                        CobblemonAtlases.EGG_PATTERN_ATLAS.atlas.height
                    )?.createModel()
                    overlayTextureModel?.setRotation(Axis.Z_AXIS.ordinal, Math.toRadians(180.0).toFloat())
                    overlayTextureModel?.render(
                        matrixStack,
                        bufferBuilder,
                        light,
                        overlay,
                        overlayColor.red.toFloat() / 255F,
                        overlayColor.green.toFloat() / 255F,
                        overlayColor.blue.toFloat() / 255F,
                        1.0F
                    )
                }

            }



            val bufferBuilderFinal = bufferBuilder.end()
            buffer.bind()
            buffer.upload(bufferBuilderFinal)
            VertexBuffer.unbind()
        }
    }
}