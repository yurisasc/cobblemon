package com.cobblemon.mod.common.client.render.entity

import com.cobblemon.mod.common.entity.fallingstar.FallingStarEntity
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.client.render.entity.EntityRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.util.Identifier

class FallingStarRenderer(ctx: EntityRendererFactory.Context?) : EntityRenderer<FallingStarEntity>(ctx) {
    override fun getTexture(entity: FallingStarEntity): Identifier {
        return cobblemonResource("textures/red")
    }
}