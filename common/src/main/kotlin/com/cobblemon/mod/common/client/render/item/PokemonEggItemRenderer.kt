package com.cobblemon.mod.common.client.render.item

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.gui.drawPortraitPokemon
import com.cobblemon.mod.common.api.gui.drawText
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.pokemon.breeding.Egg
import com.cobblemon.mod.common.client.CobblemonClient.overlay
import com.cobblemon.mod.common.client.render.models.blockbench.repository.MiscModelRepository
import com.cobblemon.mod.common.client.render.models.blockbench.setRotation
import com.cobblemon.mod.common.pokemon.Species
import com.cobblemon.mod.common.util.DataKeys
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.math.geometry.Axis
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.text.Text
import net.minecraft.util.math.RotationAxis
import java.awt.Color

//import org.lwjgl.opengl.GREMEDYStringMarker

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
        val model = MiscModelRepository.modelOf(cobblemonResource("plane.geo"))
        val texture = cobblemonResource("textures/egg_patterns/test_pattern.png")
        val layer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(texture))
        model?.setRotation(Axis.X_AXIS.ordinal, Math.toRadians(90.0).toFloat())
        model?.setRotation(Axis.Z_AXIS.ordinal, Math.toRadians(180.0).toFloat())
        //matrices.scale(1F/16F, 1F/16F, 0F)

        model?.render(matrices, layer, LightmapTextureManager.MAX_LIGHT_COORDINATE, overlay)
    }
}