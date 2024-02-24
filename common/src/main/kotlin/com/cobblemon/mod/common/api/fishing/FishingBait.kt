/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.fishing

import com.cobblemon.mod.common.registry.ItemTagCondition
import net.minecraft.util.Identifier

data class FishingBait(
    val item: Identifier?,
    val tag: ItemTagCondition? = null,
    val effect: String? = null,
    val subcategory: String? = null,
    val chance: Double? = 0.0,
    val value: Double? = 0.0,
    val note: String? = null
)
