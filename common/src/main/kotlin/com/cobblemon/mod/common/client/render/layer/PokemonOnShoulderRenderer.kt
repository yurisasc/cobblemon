/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.layer

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonFloatingState
import com.cobblemon.mod.common.client.render.models.blockbench.repository.PokemonModelRepository
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.pokemon.FormData
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.Species
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
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.util.Identifier
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
            val uuid = this.extractUuid(compoundTag)
            val cache = this.playerCache.getOrPut(livingEntity.uuid) { ShoulderCache() }
            val shoulderData: ShoulderData
            if (pLeftShoulder && cache.lastKnownLeft?.uuid != uuid) {
                shoulderData = this.extractData(compoundTag, uuid)
                cache.lastKnownLeft = shoulderData
            }
            else if (!pLeftShoulder && cache.lastKnownRight?.uuid != uuid) {
                shoulderData = this.extractData(compoundTag, uuid)
                cache.lastKnownRight = shoulderData
            }
            else {
                // should never be null but might as well be safe
                shoulderData = (if (pLeftShoulder) cache.lastKnownLeft else cache.lastKnownRight) ?: return
            }
            val scale = shoulderData.form.baseScale * shoulderData.scaleModifier
            val width = shoulderData.form.hitbox.width
            val offset = width / 2 - 0.7
            matrixStack.translate(
                if (pLeftShoulder) -offset else offset,
                (if (livingEntity.isSneaking) -1.3 else -1.5) * scale,
                0.0
            )
            matrixStack.scale(scale, scale, scale)
            val model = PokemonModelRepository.getPoser(shoulderData.species.resourceIdentifier, shoulderData.aspects)
            val state = PokemonFloatingState()
            state.updatePartialTicks(livingEntity.age.toFloat() * 20 + partialTicks)
            val vertexConsumer = buffer.getBuffer(model.getLayer(PokemonModelRepository.getTexture(shoulderData.species.resourceIdentifier, shoulderData.aspects, state)))
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
            model.withLayerContext(buffer, state, PokemonModelRepository.getLayers(shoulderData.species.resourceIdentifier, shoulderData.aspects)) {
                model.render(matrixStack, vertexConsumer, packedLight, OverlayTexture.DEFAULT_UV, 1F, 1F, 1F, 1F)
            }
            matrixStack.pop()
        }
    }

    private fun extractUuid(shoulderNbt: NbtCompound): UUID {
        if (!shoulderNbt.contains(DataKeys.SHOULDER_UUID)) {
            return shoulderNbt.getCompound(DataKeys.POKEMON).getUuid(DataKeys.POKEMON_UUID)
        }
        return shoulderNbt.getUuid(DataKeys.SHOULDER_UUID)
    }

    private fun extractData(shoulderNbt: NbtCompound, pokemonUUID: UUID): ShoulderData {
        // To not crash with existing ones, this will still have the aspect issue
        if (!shoulderNbt.contains(DataKeys.SHOULDER_SPECIES)) {
            val pokemon = Pokemon().apply { isClient = true }.loadFromNBT(shoulderNbt.getCompound(DataKeys.POKEMON))
            return ShoulderData(pokemonUUID, pokemon.species, pokemon.form, pokemon.aspects, pokemon.scaleModifier)
        }
        val species = PokemonSpecies.getByIdentifier(Identifier(shoulderNbt.getString(DataKeys.SHOULDER_SPECIES)))!!
        val formName = shoulderNbt.getString(DataKeys.SHOULDER_FORM)
        val form = species.forms.firstOrNull { it.name == formName } ?: species.standardForm
        val aspects = shoulderNbt.getList(DataKeys.SHOULDER_ASPECTS, NbtElement.STRING_TYPE.toInt()).map { it.asString() }.toSet()
        val scaleModifier = shoulderNbt.getFloat(DataKeys.SHOULDER_SCALE_MODIFIER)
        return ShoulderData(pokemonUUID, species, form, aspects, scaleModifier)
    }

    private data class ShoulderCache(
        var lastKnownLeft: ShoulderData? = null,
        var lastKnownRight: ShoulderData? = null
    )

    private data class ShoulderData(
        val uuid: UUID,
        val species: Species,
        val form: FormData,
        val aspects: Set<String>,
        val scaleModifier: Float
    )

}