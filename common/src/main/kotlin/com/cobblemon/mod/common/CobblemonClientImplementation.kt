/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common

import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.client.color.block.BlockColorProvider
import net.minecraft.client.color.item.ItemColorProvider
import java.util.function.Supplier
import net.minecraft.client.model.TexturedModelData
import net.minecraft.client.particle.ParticleFactory
import net.minecraft.client.particle.SpriteProvider
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleType
import net.minecraft.item.Item

interface CobblemonClientImplementation {
    fun registerLayer(modelLayer: EntityModelLayer, supplier: Supplier<TexturedModelData>)
    fun <T : ParticleEffect> registerParticleFactory(
        type: ParticleType<T>,
        factory: (SpriteProvider) -> ParticleFactory<T>
    )

    fun registerBlockRenderType(layer: RenderLayer, vararg blocks: Block)

    fun registerItemColors(provider: ItemColorProvider, vararg items: Item)

    fun registerBlockColors(provider: BlockColorProvider, vararg blocks: Block)

    fun <T : BlockEntity> registerBlockEntityRenderer(type: BlockEntityType<T>, factory: BlockEntityRendererFactory<T>)

}