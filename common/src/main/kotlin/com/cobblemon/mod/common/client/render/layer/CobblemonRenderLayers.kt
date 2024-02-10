/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.layer

import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.*
import net.minecraft.client.render.RenderPhase.*
import net.minecraft.data.client.TextureMap.texture

object CobblemonRenderLayers {
    val BERRY_LAYER = run {
        val multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
            .lightmap(ENABLE_LIGHTMAP)
            .program(CUTOUT_PROGRAM)
            .texture(Texture(
                cobblemonResource("textures/atlas/berries.png"),
                false,
                true
            ))
            .cull(DISABLE_CULLING)
            .build(true)
        RenderLayer.of(
            "berries",
            VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
            VertexFormat.DrawMode.QUADS,
            512,
            true,
            false,
            multiPhaseParameters
        )
    }
    val EGG_LAYER = run {
        val multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
        .lightmap(ENABLE_LIGHTMAP)
        .program(CUTOUT_PROGRAM)
        .texture(Texture(
            cobblemonResource("textures/atlas/egg_patterns.png"),
            false,
            true
        ))
        .cull(DISABLE_CULLING)
        .build(true)
        RenderLayer.of(
            "egg_patterns",
            VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
            VertexFormat.DrawMode.QUADS,
            512,
            true,
            false,
            multiPhaseParameters
        )
    }

    //For overlaying the 2D sprites for eggs in inventory
    val EGG_SPRITE_LAYER = run {
        val multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
            .program(POSITION_COLOR_TEXTURE_PROGRAM)
            .texture(Texture(
                cobblemonResource("textures/atlas/egg_pattern_sprites.png"),
                false,
                false
            ))
            .cull(DISABLE_CULLING)
            .build(true)
        RenderLayer.of(
            "egg_patterns",
            VertexFormats.POSITION_COLOR_TEXTURE,
            VertexFormat.DrawMode.QUADS,
            512,
            true,
            false,
            multiPhaseParameters
        )
    }

}