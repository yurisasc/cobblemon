/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.snowstorm

import com.cobblemon.mod.common.client.render.shader.CobblemonShaders
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.particle.ParticleTextureSheet
import net.minecraft.client.render.*
import net.minecraft.client.texture.SpriteAtlasTexture
import net.minecraft.client.texture.TextureManager

object ParticleMaterials {
    val ALPHA = object : ParticleTextureSheet {
        override fun begin(tessellator: Tessellator, textureManager: TextureManager): BufferBuilder {
            RenderSystem.enableBlend()
            RenderSystem.depthMask(true)
            RenderSystem.setShader { CobblemonShaders.PARTICLE_BLEND }
            RenderSystem.setShaderTexture(0, SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE)
            RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA)
            return tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT)
        }

        override fun toString() = "ALPHA"
    }

    val ADD = object : ParticleTextureSheet {
        override fun begin(tessellator: Tessellator, textureManager: TextureManager): BufferBuilder {
            RenderSystem.enableBlend()
            RenderSystem.depthMask(true)
            RenderSystem.setShader { CobblemonShaders.PARTICLE_BLEND }
            RenderSystem.setShaderTexture(0, SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE)
            RenderSystem.blendFunc(GlStateManager.SrcFactor.DST_COLOR, GlStateManager.DstFactor.ZERO)
            return tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT)
        }

        override fun toString() = "ADD"
    }

    val BLEND = object : ParticleTextureSheet {
        override fun begin(tessellator: Tessellator, textureManager: TextureManager): BufferBuilder {
            RenderSystem.enableBlend()
            RenderSystem.depthMask(true)
            RenderSystem.setShader { CobblemonShaders.PARTICLE_BLEND }
            RenderSystem.setShaderTexture(0, SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE)
            RenderSystem.blendFunc(GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE)
            return tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT)
        }

        override fun toString() = "BLEND"
    }

    val OPAQUE = object : ParticleTextureSheet {
        override fun begin(tessellator: Tessellator, textureManager: TextureManager): BufferBuilder {
            RenderSystem.enableBlend()
            RenderSystem.depthMask(true)
            RenderSystem.setShader { CobblemonShaders.PARTICLE_BLEND }
            RenderSystem.setShaderTexture(0, SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE)
            RenderSystem.blendFunc(GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO)
            return tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT)
        }

        override fun toString() =  "OPAQUE"
    }
}