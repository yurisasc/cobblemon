/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.api.pokemon.status

import net.minecraft.util.Identifier

/**
 * Represents the base of a status
 *
 * @author Deltric
 */
open class Status(
    val name: Identifier,
    val showdownName: String = ""
) {

}