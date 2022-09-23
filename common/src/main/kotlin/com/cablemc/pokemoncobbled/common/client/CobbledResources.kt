/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.client

import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.util.Identifier

object CobbledResources {
    /**
     * Textures
     */
    val RED = cobbledResource("textures/red.png")
    val WHITE = cobbledResource("textures/white.png")
    val PHASE_BEAM = cobbledResource("textures/phase_beam.png")

    /**
     * Fonts
     */
    val DEFAULT_LARGE = Identifier("uniform")
}