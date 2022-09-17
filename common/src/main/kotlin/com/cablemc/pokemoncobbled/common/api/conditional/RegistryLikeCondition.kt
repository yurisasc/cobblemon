/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.api.conditional

import net.minecraft.util.registry.Registry

/**
 * A condition which applies to an entry in some [Registry].
 *
 * @author Hiroku
 * @since July 16th, 2022
 */
interface RegistryLikeCondition<T> {
    fun fits(t: T, registry: Registry<T>): Boolean
}