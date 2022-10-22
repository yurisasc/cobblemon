/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client

import com.cablemc.pokemod.common.util.pokemodResource
import net.minecraft.util.Identifier

object PokemodResources {
    /**
     * Textures
     */
    val RED = pokemodResource("textures/red.png")
    val WHITE = pokemodResource("textures/white.png")
    val PHASE_BEAM = pokemodResource("textures/phase_beam.png")

    /**
     * Fonts
     */
    val DEFAULT_LARGE = Identifier("uniform")
}