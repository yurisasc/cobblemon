/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.battles.dispatch

/**
 * A dispatch that holds the battle until the condition is met.
 *
 * @author Hiroku
 * @since July 31st, 2022
 */
class UntilDispatch(val condition: () -> Boolean) : DispatchResult {
    override fun canProceed() = condition()
}