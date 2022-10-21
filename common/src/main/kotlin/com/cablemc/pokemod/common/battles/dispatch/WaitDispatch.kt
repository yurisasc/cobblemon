/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.battles.dispatch
class WaitDispatch(delaySeconds: Float) : DispatchResult {
    val readyTime = System.currentTimeMillis() + (delaySeconds * 1000).toInt()
    override fun canProceed() = System.currentTimeMillis() >= readyTime
}