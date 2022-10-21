/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.api.data

/**
 * Provides a general listener for resource and data pack updates notifying the [DataRegistry] listening.
 *
 * @author Licious
 * @since August 5th, 2022
 */
interface DataProvider {

    /**
     * Registers a [DataRegistry] to listen for updates.
     * The updates will automatically happen on the correct sides based on [DataRegistry.type].
     *
     * @param registry The [DataRegistry] being registered.
     */
    fun register(registry: DataRegistry)

}