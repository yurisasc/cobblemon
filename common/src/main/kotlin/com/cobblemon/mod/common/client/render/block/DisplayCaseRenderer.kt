/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.block

import com.cobblemon.mod.common.CobblemonBlocks
import com.cobblemon.mod.common.block.DisplayCaseBlock
import com.cobblemon.mod.common.block.DisplayCaseBlock.PositioningType
import com.cobblemon.mod.common.block.entity.displaycase.DisplayCaseBlockEntity
import com.cobblemon.mod.common.client.render.models.blockbench.repository.PokemonModelRepository
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.item.PokemonItem
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.util.math.Direction
import net.minecraft.util.math.RotationAxis

class DisplayCaseRenderer(ctx: BlockEntityRendererFactory.Context) : BlockEntityRenderer<DisplayCaseBlockEntity> {
    override fun render(
        entity: DisplayCaseBlockEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        val stack = entity.getStack()
        val world = entity.world ?: return
        val posType = DisplayCaseBlock.getPositioningType(stack, world)
        val blockState = if (entity.world != null) entity.cachedState
            else (CobblemonBlocks.DISPLAY_CASE.defaultState.with(HorizontalFacingBlock.FACING, Direction.NORTH))
        val yRot = if (posType == PositioningType.ITEM_MODEL) blockState.get(HorizontalFacingBlock.FACING).opposite.asRotation()
            else blockState.get(HorizontalFacingBlock.FACING).asRotation()

        if (stack.item is PokemonItem) {
            renderPokemon(
                matrices,
                vertexConsumers,
                light,
                stack,
                yRot
            )
            return
        }

        matrices.push()
        matrices.translate(0.5f, 0.4f, 0.5f)

        matrices.scale(posType.scaleX, posType.scaleY, posType.scaleZ)
        matrices.translate(posType.transX, posType.transY, posType.transZ)

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-yRot))

        MinecraftClient.getInstance().itemRenderer.renderItem(
            stack,
            ModelTransformationMode.GROUND,
            light,
            overlay,
            matrices,
            vertexConsumers,
            entity.world,
            0
        )

        matrices.pop()

    }

    private fun renderPokemon(
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        stack: ItemStack,
        yRot: Float
    ) {
        val item = stack.item as? PokemonItem ?: return
        val pokemon = item.asPokemon(stack) ?: return
        val model = PokemonModelRepository.getPoser(pokemon.species.resourceIdentifier, pokemon.aspects)
        val renderLayer = model.getLayer(PokemonModelRepository.getTexture(pokemon.species.resourceIdentifier, pokemon.aspects, 0F))
        val tint = item.tint(stack)
        val vertexConsumer: VertexConsumer = vertexConsumers.getBuffer(renderLayer)
        val scale = 0.25f

        matrices.push()
        matrices.scale(1f, -1f, -1f)
        matrices.translate(0.5f, -0.69f, -0.5f)
        matrices.scale(scale, scale, scale)
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yRot))

        model.setupAnimStateless(PoseType.PROFILE)

        model.withLayerContext(vertexConsumers, null, PokemonModelRepository.getLayers(pokemon.species.resourceIdentifier, pokemon.aspects)) {
            model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, tint.x, tint.y, tint.z, tint.w)
        }

        matrices.pop()
    }
}