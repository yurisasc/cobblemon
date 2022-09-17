/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.api.item

import net.minecraft.util.Identifier

class Berry(
    val name: Identifier,
    val spicy: Int,
    val dry: Int,
    val sweet: Int,
    val bitter: Int,
    val sour: Int
) {

}