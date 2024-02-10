package com.cobblemon.mod.common.client.render.item

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.gui.drawPortraitPokemon
import com.cobblemon.mod.common.api.gui.drawText
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.pokemon.breeding.Egg
import com.cobblemon.mod.common.client.CobblemonClient.overlay
import com.cobblemon.mod.common.client.render.layer.CobblemonRenderLayers
import com.cobblemon.mod.common.client.render.models.blockbench.repository.MiscModelRepository
import com.cobblemon.mod.common.client.render.models.blockbench.setRotation
import com.cobblemon.mod.common.pokemon.Species
import com.cobblemon.mod.common.util.DataKeys
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.math.geometry.Axis
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.ShaderProgram
import net.minecraft.client.gl.VertexBuffer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.RenderLayers
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.TexturedRenderLayers
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound


class PokemonEggItemRenderer : CobblemonBuiltinItemRenderer {
    override fun render(
        stack: ItemStack,
        mode: ModelTransformationMode,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        val egg = Egg.fromNbt(stack.nbt?.get(DataKeys.EGG) as NbtCompound)
        if (mode == ModelTransformationMode.GUI) {
            renderGui(egg, stack, matrices, vertexConsumers, light, overlay)
        }
    }

    //The way this is done is dumb af, should rewrite using blitk/2d drawing
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
        val buffer = Tessellator.getInstance().buffer
        val layer = CobblemonRenderLayers.EGG_SPRITE_LAYER
        buffer.begin(VertexFormat.DrawMode.QUADS, CobblemonRenderLayers.EGG_SPRITE_LAYER.vertexFormat)
        //Do not mess with the winding order - or else >:(
        buffer.vertex(0.0, 1.0, 1.0)
        buffer.texture(0f, 0f)
        buffer.next()
        buffer.vertex(0.0, 0.0, 1.0)
        buffer.texture(0f, 1f)
        buffer.next()
        buffer.vertex(1.0, 0.0, 1.0)
        buffer.texture(1f, 1f)
        buffer.next()
        buffer.vertex(1.0, 1.0, 1.0)
        buffer.texture(1f, 0f)
        buffer.next()
        val builtBuffer = buffer.end()
        val vertexBuffer = VertexBuffer(VertexBuffer.Usage.STATIC)
        vertexBuffer.bind()
        vertexBuffer.upload(builtBuffer)
        layer.startDrawing()
        vertexBuffer.bind()
        vertexBuffer.draw(
            posMatrix,
            RenderSystem.getProjectionMatrix(),
            GameRenderer.getPositionTexProgram()
        )
        vertexBuffer.close()
        VertexBuffer.unbind()

        layer.endDrawing()
        matrices.pop()
        //cobblemonResource("textures/egg_patterns/test_pattern.png")
    }
}