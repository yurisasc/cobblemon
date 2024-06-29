/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common

import com.cobblemon.mod.common.client.render.ModelLayer
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.client.color.block.BlockColorProvider
import net.minecraft.client.color.item.ItemColorProvider
import net.minecraft.client.model.TexturedModelData
import net.minecraft.client.particle.ParticleFactory
import net.minecraft.client.particle.SpriteProvider
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.render.block.entity.BlockEntityRendererProvider
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.render.entity.model.ModelLayerLocation
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.item.Item
import net.minecraft.particle.ParticleEffect
import java.util.function.Supplier
import net.minecraft.client.particle.SpriteSet
import net.minecraft.core.particles.ParticleType

interface CobblemonClientImplementation {
    fun registerLayer(modelLayer: ModelLayer, supplier: Supplier<TexturedModelData>)
    fun <T : ParticleEffect> registerParticleFactory(
        type: ParticleType<T>,
        factory: (SpriteSet) -> ParticleFactory<T>
    )

    fun registerBlockRenderType(layer: RenderType, vararg blocks: Block)

    fun registerItemColors(provider: ItemColorProvider, vararg items: Item)

    fun registerBlockColors(provider: BlockColorProvider, vararg blocks: Block)

    fun <T : BlockEntity> registerBlockEntityRenderer(type: BlockEntityType<out T>, factory: BlockEntityRendererProvider<T>)

    fun <T : Entity> registerEntityRenderer(type: EntityType<out T>, factory: EntityRendererProvider<T>)
}