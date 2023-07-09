/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.layer

import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonFloatingState
import com.cobblemon.mod.common.client.render.models.blockbench.repository.PokemonModelRepository
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.DataKeys
import com.cobblemon.mod.common.util.isPokemonEntity
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.LivingEntityRenderer
import net.minecraft.client.render.entity.feature.FeatureRenderer
import net.minecraft.client.render.entity.feature.FeatureRendererContext
import net.minecraft.client.render.entity.model.PlayerEntityModel
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerEntity
import java.util.*

class PokemonOnShoulderRenderer<T : PlayerEntity>(renderLayerParent: FeatureRendererContext<T, PlayerEntityModel<T>>) : FeatureRenderer<T, PlayerEntityModel<T>>(renderLayerParent) {

    private val playerCache = hashMapOf<UUID, ShoulderCache>()

    override fun render(
        matrixStack: MatrixStack,
        buffer: VertexConsumerProvider,
        packedLight: Int,
        livingEntity: T,
        limbSwing: Float,
        limbSwingAmount: Float,
        partialTicks: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        this.render(matrixStack, buffer, packedLight, livingEntity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, true)
        this.render(matrixStack, buffer, packedLight, livingEntity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, false)
    }

    private fun render(
        matrixStack: MatrixStack,
        buffer: VertexConsumerProvider,
        packedLight: Int,
        livingEntity: T,
        limbSwing: Float,
        limbSwingAmount: Float,
        partialTicks: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float,
        pLeftShoulder: Boolean
    ) {
        val compoundTag = if (pLeftShoulder) livingEntity.shoulderEntityLeft else livingEntity.shoulderEntityRight
        if (compoundTag.isPokemonEntity()) {
            matrixStack.push()
            val uuid = compoundTag.getCompound(DataKeys.POKEMON).getUuid(DataKeys.POKEMON_UUID)
            val cache = this.playerCache.getOrPut(livingEntity.uuid) { ShoulderCache() }
            val pokemon: Pokemon
            if (pLeftShoulder && cache.lastKnownLeft?.uuid != uuid) {
                pokemon = Pokemon().also { it.isClient = true }.loadFromNBT(compoundTag.getCompound(DataKeys.POKEMON))
                cache.lastKnownLeft = pokemon
            }
            else if (!pLeftShoulder && cache.lastKnownRight?.uuid != uuid) {
                pokemon = Pokemon().also { it.isClient = true }.loadFromNBT(compoundTag.getCompound(DataKeys.POKEMON))
                cache.lastKnownRight = pokemon
            }
            else {
                // should never be null but might as well be safe
                pokemon = (if (pLeftShoulder) cache.lastKnownLeft else cache.lastKnownRight) ?: Pokemon()
            }
            val scale = pokemon.form.baseScale * pokemon.scaleModifier
            val width = pokemon.form.hitbox.width
            val offset = width / 2 - 0.7
            matrixStack.translate(
                if (pLeftShoulder) -offset else offset,
                (if (livingEntity.isSneaking) -1.3 else -1.5) * scale,
                0.0
            )
            matrixStack.scale(scale, scale, scale)
            val model = PokemonModelRepository.getPoser(pokemon.species.resourceIdentifier, pokemon.aspects)
            val state = PokemonFloatingState()
            state.updatePartialTicks(livingEntity.age.toFloat() * 20 + partialTicks)
            val vertexConsumer = buffer.getBuffer(model.getLayer(PokemonModelRepository.getTexture(pokemon.species.resourceIdentifier, pokemon.aspects, state)))
            val i = LivingEntityRenderer.getOverlay(livingEntity, 0.0f)

            val pose = model.poses.values
                .firstOrNull { (if (pLeftShoulder) PoseType.SHOULDER_LEFT else PoseType.SHOULDER_RIGHT) in it.poseTypes  }
                ?: model.poses.values.first()
            state.setPose(pose.poseName)
            state.timeEnteredPose = 0F
            model.setupAnimStateful(
                entity = null,
                state = state,
                headYaw = netHeadYaw,
                headPitch = headPitch,
                limbSwing = limbSwing,
                limbSwingAmount = limbSwingAmount,
                ageInTicks = livingEntity.age.toFloat()
            )
            model.render(matrixStack, vertexConsumer, packedLight, i, 1.0f, 1.0f, 1.0f, 1.0f)
            model.withLayerContext(buffer, state, PokemonModelRepository.getLayers(pokemon.species.resourceIdentifier, pokemon.aspects)) {
                model.render(matrixStack, vertexConsumer, packedLight, OverlayTexture.DEFAULT_UV, 1F, 1F, 1F, 1F)
            }
            matrixStack.pop();
        }
    }

    private data class ShoulderCache(var lastKnownLeft: Pokemon? = null, var lastKnownRight: Pokemon? = null)

}