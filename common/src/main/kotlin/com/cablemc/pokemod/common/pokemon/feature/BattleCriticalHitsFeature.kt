/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.pokemon.feature

/**
 * A feature that keeps track of critical hits during battles.
 * They reset every battle.
 *
 * @author Licious
 * @since October 2nd, 2022
 */
class BattleCriticalHitsFeature : ResettableAmountFeature() {

    override fun createInstance(value: Int) = BattleCriticalHitsFeature().apply { currentValue = value }

    override val name: String = ID

    companion object {
        const val ID = "battle_critical_hits"
    }

}