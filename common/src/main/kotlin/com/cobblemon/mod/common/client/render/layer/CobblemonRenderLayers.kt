/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.layer

import com.cobblemon.mod.common.util.cobblemonResource
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.RenderPhase.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Util
import java.util.function.BiFunction
import java.util.function.Function
import net.minecraft.Util
import net.minecraft.client.renderer.RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_SHADER
import net.minecraft.client.renderer.RenderStateShard.TRANSLUCENT_TRANSPARENCY
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard

object CobblemonRenderLayers {
    val BERRY_LAYER = run {
        val multiPhaseParameters = RenderType.MultiPhaseParameters.builder()
            .lightmap(ENABLE_LIGHTMAP)
            .program(CUTOUT_PROGRAM)
            .texture(Texture(
                cobblemonResource("textures/atlas/berries.png"),
                false,
                true
            ))
            .cull(DISABLE_CULLING)
            .build(true)
        RenderType.create(
            "berries",
            DefaultVertexFormat.BLOCK,
            VertexFormat.Mode.QUADS,
            512,
            true,
            false,
            multiPhaseParameters
        )
    }

    val ENTITY_TRANSLUCENT: BiFunction<ResourceLocation, Boolean, RenderType> = Util.memoize { texture: ResourceLocation, affectsOutline: kotlin.Boolean ->
        var multiPhaseParameters: RenderType.CompositeState =
            RenderType.CompositeState.builder()
                .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
                .setTextureState(TextureStateShard(texture, false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .cull(DISABLE_CULLING)
                .lightmap(ENABLE_LIGHTMAP)
                .overlay(ENABLE_OVERLAY_COLOR).build(affectsOutline)
        RenderType.of(
            "entity_translucent",
            DefaultVertexFormat.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
            VertexFormat.Mode.QUADS,
            256,
            true,
            true,
            multiPhaseParameters
        )
    };

    val ENTITY_CUTOUT: Function<ResourceLocation, RenderType> = Util.memoize { texture: ResourceLocation ->
        val multiPhaseParameters =
            RenderType.MultiPhaseParameters.builder()
                .program(ENTITY_CUTOUT_PROGRAM)
                .texture(Texture(texture, false, false))
                .transparency(NO_TRANSPARENCY)
                .lightmap(ENABLE_LIGHTMAP)
                .overlay(ENABLE_OVERLAY_COLOR).build(true)
        RenderType.of(
            "entity_cutout",
            VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
            VertexFormat.Mode.QUADS,
            256,
            true,
            false,
            multiPhaseParameters
        )
    };

}