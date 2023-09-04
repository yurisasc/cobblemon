package com.cobblemon.mod.common.client.render.layer

import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.*
import net.minecraft.client.render.RenderPhase.*

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

}