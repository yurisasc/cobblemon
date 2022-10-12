/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.api.reference

/**
 * Reference data interface. Reference data is defined as information that applies to Pokémon Cobbled structurally,
 * such as species, stat types, Pokémon types, etc. These do not change during runtime, and are instead used only
 * referentially.
 *
 * A Pokémon Cobbled class or interface that implements this interface is capable of being added/modified/deleted
 * on the server side and this information will reach the client on login.
 */
interface ReferenceData {
    /**
     * A unique identifier for this piece of reference data among this reference collection.
     * Used to identify when two pieces of reference data are describing the same thing.
     */
    fun id(): String
    /** Generate a hash to be able to tell if the server and client have a differing version of this. */
    override fun hashCode(): Int

    // TODO write and read from byte buffer
}