/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.npc

import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatefulAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatelessAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.ModelFrame
import com.cobblemon.mod.common.entity.npc.NPCEntity



@FunctionalInterface
fun interface TrainerAnimationProvider {
    operator fun invoke(state: PoseableEntityState<NPCEntity>): StatefulAnimation<NPCEntity, ModelFrame>?
}

@FunctionalInterface
fun interface TrainerStatelessAnimationProvider {
    operator fun invoke(entity: NPCEntity?, state: PoseableEntityState<NPCEntity>?): StatelessAnimation<NPCEntity, ModelFrame>?
}