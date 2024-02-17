/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

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