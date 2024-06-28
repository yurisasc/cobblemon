/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.entity

import com.bedrockk.molang.runtime.struct.QueryStruct
import com.sun.xml.internal.stream.Entity
import net.minecraft.network.syncher.EntityDataAccessor

/**
 * Represents a delegation of a portion of an entity's logic to a particular side.
 */
interface EntitySideDelegate<T : Entity> {
    fun initialize(entity: T) {}
    fun tick(entity: T) {}
    fun onTrackedDataSet(data: EntityDataAccessor<*>) {}
    fun addToStruct(struct: QueryStruct) {}
}