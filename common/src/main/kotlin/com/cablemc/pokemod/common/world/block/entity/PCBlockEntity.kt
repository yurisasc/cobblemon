/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.world.block.entity

import com.cablemc.pokemod.common.PokemodBlockEntities
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.math.BlockPos

class PCBlockEntity(
    blockPos: BlockPos,
    blockState: BlockState
) : BlockEntity(PokemodBlockEntities.PC.get(), blockPos, blockState)