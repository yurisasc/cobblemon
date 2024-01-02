package com.cobblemon.mod.common.client.render.block

import com.cobblemon.mod.common.api.pokemon.breeding.Egg
import com.cobblemon.mod.common.api.pokemon.breeding.EggPatterns
import com.cobblemon.mod.common.block.BerryBlock
import com.cobblemon.mod.common.block.entity.BerryBlockEntity
import com.cobblemon.mod.common.block.entity.NestBlockEntity
import com.cobblemon.mod.common.client.render.atlas.CobblemonAtlases
import com.cobblemon.mod.common.client.render.layer.CobblemonRenderLayers
import com.cobblemon.mod.common.client.render.models.blockbench.repository.BerryModelRepository
import com.cobblemon.mod.common.client.render.models.blockbench.repository.EggModelRepo
import com.cobblemon.mod.common.client.render.models.blockbench.setPosition
import com.cobblemon.mod.common.client.render.models.blockbench.setRotation
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.math.geometry.Axis
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gl.VertexBuffer
import net.minecraft.client.model.ModelPart
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.RenderPhase.Overlay
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.ColorHelper
import net.minecraft.util.math.RotationAxis
import net.minecraft.util.math.Vec3d
import org.joml.Vector3f
import java.awt.Color

class NestBlockRenderer(private val context: BlockEntityRendererFactory.Context) : BlockEntityRenderer<NestBlockEntity> {
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
        if (renderState.needsRebuild || renderState.vboLightLevel != light) {
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
            val model = EggModelRepo.eggModels[cobblemonResource("egg")]
            val pattern = EggPatterns.patternMap[egg?.patternId]!!
            val primaryTexture = pattern.primaryTexturePath
            val secondaryTexture = pattern.secondaryTexturePath
            val primaryAtlasedTexture = CobblemonAtlases.EGG_PATTERN_ATLAS.getSprite(primaryTexture)
            val secondaryAtlasedTexture =
                CobblemonAtlases.EGG_PATTERN_ATLAS.getSprite(secondaryTexture)
            val primaryColor = Color.decode(egg?.primaryColor)
            val secondaryColor = Color.decode(egg?.secondaryColor)

            //Patching uvs so we can use atlases
            val baseModel = model?.create(false)?.createModel()
            val primaryTextureModel = model?.createWithUvOverride(
                false,
                primaryAtlasedTexture.x,
                primaryAtlasedTexture.y,
                CobblemonAtlases.EGG_PATTERN_ATLAS.atlas.width,
                CobblemonAtlases.EGG_PATTERN_ATLAS.atlas.height
            )?.createModel()
            val secondaryTextureModel = model?.createWithUvOverride(
                false,
                secondaryAtlasedTexture.x,
                secondaryAtlasedTexture.y,
                CobblemonAtlases.EGG_PATTERN_ATLAS.atlas.width,
                CobblemonAtlases.EGG_PATTERN_ATLAS.atlas.height
            )?.createModel()

            baseModel?.render(MatrixStack(), bufferBuilder, light, overlay)
            primaryTextureModel?.render(
                MatrixStack(),
                bufferBuilder,
                light,
                ColorHelper.Argb.getArgb(1, primaryColor.red, primaryColor.green, primaryColor.blue)
            )
            secondaryTextureModel?.render(
                MatrixStack(),
                bufferBuilder,
                light,
                ColorHelper.Argb.getArgb(
                    1,
                    secondaryColor.red,
                    secondaryColor.green,
                    secondaryColor.blue
                )
            )


            val bufferBuilderFinal = bufferBuilder.end()
            buffer.bind()
            buffer.upload(bufferBuilderFinal)
            VertexBuffer.unbind()
        }
    }
}