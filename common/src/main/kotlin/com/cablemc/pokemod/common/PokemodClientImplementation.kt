/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common

import java.util.function.Supplier
import net.minecraft.client.model.TexturedModelData
import net.minecraft.client.render.entity.model.EntityModelLayer

interface PokemodClientImplementation {
    fun registerLayer(modelLayer: EntityModelLayer, supplier: Supplier<TexturedModelData>)
}