/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.world.level.block

import com.cablemc.pokemoncobbled.common.world.level.block.grower.ApricornTreeGrower
import net.minecraft.block.SaplingBlock

class ApricornSaplingBlock(properties : Settings, color: String) : SaplingBlock(ApricornTreeGrower(color), properties)