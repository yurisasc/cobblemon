package com.cobblemon.mod.common.client.render.layer

import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.client.render.*
import net.minecraft.client.render.RenderPhase.*

object CobblemonRenderLayers {
    val BERRY_LAYER = run {
        val multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
            .program(ENTITY_CUTOUT_NONULL_PROGRAM)
            .texture(Texture(cobblemonResource("textures/atlas/berries.png"), false, true))
            .transparency(NO_TRANSPARENCY)
            .cull(Cull(false))
            .lightmap(ENABLE_LIGHTMAP)
            .overlay(ENABLE_OVERLAY_COLOR)
            .build(false);
        RenderLayer.of(
            "berry",
            VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
            VertexFormat.DrawMode.QUADS,
            256,
            true,
            false,
            multiPhaseParameters
            )
    }

}