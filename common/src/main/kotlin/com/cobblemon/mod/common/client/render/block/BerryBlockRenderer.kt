/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.block

import com.cobblemon.mod.common.world.block.BerryBlock
import com.cobblemon.mod.common.world.block.entity.BerryBlockEntity
import kotlin.math.PI
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.Vector3d
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Matrix3f
import net.minecraft.util.math.Quaternion
import net.minecraft.util.math.Vec3f
import net.minecraft.util.math.Vector4f
import net.minecraft.util.shape.VoxelShape

class BerryBlockRenderer(private val context: BlockEntityRendererFactory.Context) : BlockEntityRenderer<BerryBlockEntity> {

    override fun render(entity: BerryBlockEntity, tickDelta: Float, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int) {
        val blockState = entity.cachedState
        val age = blockState.get(BerryBlock.AGE)
        if (age <= BerryBlock.MATURE_AGE) {
            return
        }
        matrices.push()
        val isFlower = age == BerryBlock.FLOWER_AGE
        entity.berryAndShape(isFlower).forEach { (berry, shape) ->
            val model = (if (isFlower) berry.flowerModel() else berry.fruitModel()) ?: return@forEach
            val texture = if (isFlower) berry.flowerTexture else berry.fruitTexture
            val layer = RenderLayer.getEntityCutoutNoCull(texture)
            val midX = (shape.getMin(Direction.Axis.X) + shape.getMax(Direction.Axis.X)) / 2
            /*
             * For midY we have to subtract 1.5. Here's why:
             *
             * In Minecraft's entity rendering of Java models, there's a locked in Y+24 offset in the model exports and
             * in the MobRenderer code which our PokemonRenderer extends. It's unclear why they do it but my enduring
             * theory is that it's rendering the entities upside down, but basically the MobRenderer pushes it in one
             * direction and the model's +24 pushes it back into place again.
             *
             * We extend the MobRenderer, but we use Bedrock model exports which don't do the +24. In the bedrock model
             * interpreter we had to add a hardcoded +24 to match what the renderer is going to do later, but we aren't
             * in the MobRenderer, are we?
             *
             * The 24 is in model coordinates, which are world coordinates multiplied by 16. So, 1.5.
             *
             * - Hiro
             *
             * P.S. we also swap the sign on Y because they really do normally do this shit upside down, so the -1 Y
             * scale down below has forced me to inverse the Y here as well. It's pixel-perfect though, we're good.
             */
            val midY = -(shape.getMin(Direction.Axis.Y) + shape.getMax(Direction.Axis.Y)) / 2 - 1.5
            val midZ = (shape.getMin(Direction.Axis.Z) + shape.getMax(Direction.Axis.Z)) / 2

            val vertexConsumer = vertexConsumers.getBuffer(layer)

            matrices.push()
            matrices.scale(1F, -1F, 1F)
            matrices.translate(midX, midY, midZ)
            matrices.push()
            val angleOfRotationDegrees = 30F
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(angleOfRotationDegrees))
            model.render(matrices, vertexConsumer, light, overlay)
            matrices.pop()
            matrices.pop()
        }
        matrices.pop()
    }

}