/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.npc

import net.minecraft.client.model.ModelPart

class RegularNPCModel(part: ModelPart) : NPCModel(part) {
    override val rootPart = part.registerChildWithAllChildren("model")
    override val name: String = "generic"

    override fun registerPoses() {
        TODO("Not yet implemented")
    }
}