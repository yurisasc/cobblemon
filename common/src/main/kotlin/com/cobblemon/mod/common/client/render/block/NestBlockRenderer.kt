package com.cobblemon.mod.common.client.render.block

import com.cobblemon.mod.common.api.pokemon.breeding.Egg
import com.cobblemon.mod.common.api.pokemon.breeding.EggPatterns
import com.cobblemon.mod.common.block.entity.NestBlockEntity
import com.cobblemon.mod.common.client.render.models.blockbench.repository.EggModelRepo
import com.cobblemon.mod.common.client.render.models.blockbench.setRotation
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.math.geometry.Axis
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.RenderPhase.Overlay
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.RotationAxis
import net.minecraft.util.math.Vec3d
import org.joml.Vector3f

class NestBlockRenderer(private val context: BlockEntityRendererFactory.Context) : BlockEntityRenderer<NestBlockEntity> {
    override fun render(
        entity: NestBlockEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        matrices.translate(0.0, 1.0, 0.0)
        renderEgg(
            matrices,
            vertexConsumers,
            light,
            overlay,
            null
        )
    }

    fun renderEgg(
        matrices: MatrixStack?,
        vertexConsumer: VertexConsumerProvider,
        light: Int,
        overlay: Int,
        egg: Egg?
    ) {
        val model = EggModelRepo.eggModels[cobblemonResource("egg")]
        //val texture = EggPatterns.patternMap[egg.pattern.texturePath]
        val createdModel = model?.create(false)?.createModel()
        RenderSystem.enableBlend()
        createdModel?.setRotation(Axis.Z_AXIS.ordinal, Math.toRadians(180.0).toFloat())
        createdModel?.render(matrices, vertexConsumer.getBuffer(
            RenderLayer.getEntityCutout(cobblemonResource("textures/egg_patterns/base.png"))),
            LightmapTextureManager.MAX_LIGHT_COORDINATE,
            overlay,
        )
        createdModel?.render(matrices, vertexConsumer.getBuffer(
            RenderLayer.getEntityCutout(cobblemonResource("textures/egg_patterns/test_pattern.png"))),
            LightmapTextureManager.MAX_LIGHT_COORDINATE,
            1,
        )
    }
}