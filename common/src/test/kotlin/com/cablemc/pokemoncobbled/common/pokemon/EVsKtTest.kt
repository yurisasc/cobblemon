/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.pokemon

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

internal class EVsKtTest {
    @Test
    fun `should create a empty set of EVs`() {
        val evs = EVs.createEmpty()
        assertFalse(evs.any { (_, value) -> value > 0 })
    }
}