/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.api.reactive.pipes

import com.cablemc.pokemod.common.api.reactive.Transform

/**
 * A transform which will only take some number of emissions before terminating the observable subscription.
 *
 * @author Hiroku
 * @since November 26th, 2021
 */
class TakeFirstTransform<I>(private var amount: Int = 1) : Transform<I, I> {
    override fun invoke(input: I): I {
        if (amount > 0) {
            amount--
            return input
        } else {
            noTransform(terminate = true)
        }
    }
}