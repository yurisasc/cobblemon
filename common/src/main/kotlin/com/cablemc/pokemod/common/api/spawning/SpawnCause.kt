/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.api.spawning

import com.cablemc.pokemod.common.api.spawning.spawner.Spawner
import net.minecraft.entity.Entity

open class SpawnCause(
    val spawner: Spawner,
    val bucket: SpawnBucket,
    val entity: Entity? = null
)