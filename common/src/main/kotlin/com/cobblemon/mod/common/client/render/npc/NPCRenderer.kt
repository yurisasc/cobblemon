/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.npc

import com.cobblemon.mod.common.client.entity.NPCClientDelegate
import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityModel
import com.cobblemon.mod.common.client.render.models.blockbench.npc.NPCModel
import com.cobblemon.mod.common.client.render.models.blockbench.repository.NPCModelRepository
import com.cobblemon.mod.common.entity.npc.NPCEntity
import kotlin.math.min
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRendererFactory.Context
import net.minecraft.client.render.entity.LivingEntityRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

class NPCRenderer(context: Context) : LivingEntityRenderer<NPCEntity, NPCModel>(context, null, 0.5f) {
    override fun getTexture(entity: NPCEntity): Identifier {
        return NPCModelRepository.getTexture(entity.npc.resourceIdentifier, entity.aspects.get(), entity.delegate as NPCClientDelegate)
    }

    override fun render(
        entity: NPCEntity,
        entityYaw: Float,
        partialTicks: Float,
        poseMatrix: MatrixStack,
        buffer: VertexConsumerProvider,
        packedLight: Int
    ) {
        shadowRadius = min((entity.boundingBox.maxX - entity.boundingBox.minX), (entity.boundingBox.maxZ) - (entity.boundingBox.minZ)).toFloat() / 1.5F
        model = NPCModelRepository.getPoser(entity.npc.resourceIdentifier, entity.aspects.get())

        val clientDelegate = entity.delegate as NPCClientDelegate
        val modelNow = model as PoseableEntityModel<NPCEntity>

        modelNow.setLayerContext(buffer, clientDelegate, NPCModelRepository.getLayers(entity.npc.resourceIdentifier, entity.aspects.get()))

        super.render(entity, entityYaw, partialTicks, poseMatrix, buffer, packedLight)

        modelNow.green = 1F
        modelNow.blue = 1F
        modelNow.resetLayerContext()

//        if (this.shouldRenderLabel(entity)) {
//            this.renderLabelIfPresent(entity, entity.displayName, poseMatrix, buffer, packedLight)
//        }
    }
}