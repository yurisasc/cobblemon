package com.cobblemon.mod.common.client.render.item

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.gui.drawPortraitPokemon
import com.cobblemon.mod.common.api.gui.drawText
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.pokemon.breeding.Egg
import com.cobblemon.mod.common.pokemon.Species
import com.cobblemon.mod.common.util.DataKeys
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
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
            MinecraftClient.getInstance().itemRenderer.renderItem(Items.EGG.defaultStack, ModelTransformationMode.GUI, light, overlay, matrices, vertexConsumers, MinecraftClient.getInstance().world, 0)
            renderGui(egg, stack, matrices)
        }
    }

    fun renderGui(egg: Egg, stack: ItemStack, matrices: MatrixStack) {
        //GREMEDYStringMarker.glStringMarkerGREMEDY("Rendering egg");


    }
}