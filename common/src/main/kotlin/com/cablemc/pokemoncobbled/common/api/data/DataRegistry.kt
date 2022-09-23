/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.api.data

import com.cablemc.pokemoncobbled.common.api.reactive.SimpleObservable
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourceType
import net.minecraft.util.Identifier

/**
 * A registry with data provided by a resource or data pack.
 *
 * @author Licious
 * @since August 1st, 2022
 */
interface DataRegistry {

    /**
     * The unique [Identifier] of this registry.
     */
    val id: Identifier

    /**
     * The expected [ResourceType].
     */
    val type: ResourceType

    /**
     * An observable that emits whenever this registry has finished reloading.
     */
    val observable: SimpleObservable<out DataRegistry>

    /**
     * Reloads this registry.
     *
     * @param manager The newly updated [ResourceManager]
     */
    fun reload(manager: ResourceManager)

}