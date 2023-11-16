/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block.entity.fossil

import com.cobblemon.mod.common.CobblemonBlockEntities
import com.cobblemon.mod.common.api.multiblock.builder.MultiblockStructureBuilder
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos

class FossilCompartmentBlockEntity(
    pos: BlockPos, state: BlockState,
    multiblockBuilder: MultiblockStructureBuilder
): FossilMultiblockEntity(pos, state, multiblockBuilder, CobblemonBlockEntities.FOSSIL_COMPARTMENT) {

}