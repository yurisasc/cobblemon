/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.render.models.blockbench.additives

import com.cablemc.pokemod.common.client.render.models.blockbench.PoseableEntityModel
import com.cablemc.pokemod.common.client.render.models.blockbench.PoseableEntityState
import com.cablemc.pokemod.common.client.render.models.blockbench.frame.ModelFrame
import net.minecraft.entity.Entity

/**
 * A freeform, stateful animation that can be applied to any model that
 * fits the entity type. In most cases you will want to check in the run
 * function whether the model is of the right [ModelFrame] to do what you
 * need.
 *
 * @author Hiroku
 * @since December 5th, 2021
 */
interface PosedAdditiveAnimation<T : Entity> {
    /** Runs the animation and returns true if the animation should continue. */
    fun run(entity: T?, model: PoseableEntityModel<T>, state: PoseableEntityState<T>?): Boolean
}