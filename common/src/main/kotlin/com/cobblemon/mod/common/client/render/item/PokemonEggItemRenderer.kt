package com.cobblemon.mod.common.client.render.item

import com.cobblemon.mod.common.api.pokemon.breeding.Egg
import com.cobblemon.mod.common.client.render.atlas.CobblemonAtlases
import com.cobblemon.mod.common.client.render.layer.CobblemonRenderLayers
import com.cobblemon.mod.common.util.DataKeys
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gl.VertexBuffer
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier
import org.joml.Matrix4f
import java.awt.Color


class PokemonEggItemRenderer : CobblemonBuiltinItemRenderer {
    override fun render(
        stack: ItemStack,
        mode: ModelTransformationMode,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        val egg = Egg.fromNbt(stack.nbt?.get(DataKeys.EGG) as? NbtCompound ?: return)
        if (mode == ModelTransformationMode.GUI) {
            renderGui(egg, stack, matrices, vertexConsumers, light, overlay)
        }
    }

    //We also need to optimize this a little bit, kinda bad for perf
    //Need to use VBOs like we do for BERs, but what do we attach them to?
    fun renderGui(
        egg: Egg,
        stack: ItemStack,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        matrices.push()
        //Minecraft does this big Z offset to make sure item sprites render on top
        //I discovered if you make this Z too big it does not work tho
        //That is why we divide by 16 (since the pos matrix is scaled by 16)
        matrices.translate(0.0, 0.0, -11000.0/16.0)
        val posMatrix = matrices.peek().positionMatrix
        val baseTexBuffer = renderToVertexBuffer(egg.getPattern()!!.baseInvSpritePath, Color.decode("#${egg.baseColor}"))
        val overlayTexBuffer = egg.getPattern()!!.overlayInvSpritePath?.let {
            renderToVertexBuffer(it, Color.decode("#${egg.overlayColor}"))
        }
        renderBuffer(baseTexBuffer, posMatrix, CobblemonRenderLayers.EGG_SPRITE_LAYER)
        if (overlayTexBuffer != null) {
            renderBuffer(overlayTexBuffer, posMatrix, CobblemonRenderLayers.EGG_SPRITE_LAYER)
        }
        matrices.pop()
        //cobblemonResource("textures/egg_patterns/test_pattern.png")
    }

    fun renderToVertexBuffer(textureId: Identifier, color: Color): VertexBuffer {
        val buffer = Tessellator.getInstance().buffer
        val texture = CobblemonAtlases.EGG_PATTERN_SPRITE_ATLAS.getSprite(textureId)

        buffer.begin(
            VertexFormat.DrawMode.QUADS,
            CobblemonRenderLayers.EGG_SPRITE_LAYER.vertexFormat
        )
        //Do not mess with the winding order - or else >:(
        buffer.vertex(0.0, 1.0, 1.0)
        buffer.color(color.red / 255F, color.green / 255F, color.blue / 255F, 1F)
        buffer.texture(texture.minU, texture.minV)
        buffer.next()
        buffer.vertex(0.0, 0.0, 1.0)
        buffer.color(color.red / 255F, color.green / 255F, color.blue / 255F, 1F)
        buffer.texture(texture.minU, texture.maxV)
        buffer.next()
        buffer.vertex(1.0, 0.0, 1.0)
        buffer.color(color.red / 255F, color.green / 255F, color.blue / 255F, 1F)
        buffer.texture(texture.maxU, texture.maxV)
        buffer.next()
        buffer.vertex(1.0, 1.0, 1.0)
        buffer.color(color.red / 255F, color.green / 255F, color.blue / 255F, 1F)
        buffer.texture(texture.maxU, texture.minV)
        buffer.next()
        val builtBuffer = buffer.end()
        val vertBuf = VertexBuffer(VertexBuffer.Usage.STATIC)
        vertBuf.bind()
        vertBuf.upload(builtBuffer)
        VertexBuffer.unbind()
        return vertBuf
    }

    fun renderBuffer(buffer: VertexBuffer, posMatrix: Matrix4f, layer: RenderLayer) {
        layer.startDrawing()
        buffer.bind()
        buffer.draw(
            posMatrix,
            RenderSystem.getProjectionMatrix(),
            GameRenderer.getPositionColorTexProgram()
        )
        buffer.close()
        VertexBuffer.unbind()
        layer.endDrawing()
    }
}