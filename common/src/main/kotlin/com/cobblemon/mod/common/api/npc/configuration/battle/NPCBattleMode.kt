/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.npc.configuration.battle

import com.cobblemon.mod.common.api.npc.NPCPartyProvider
import com.cobblemon.mod.common.api.npc.partyproviders.SimplePartyProvider
import com.cobblemon.mod.common.battles.BattleType
import com.cobblemon.mod.common.battles.BattleTypes

class NPCBattleMode {
    val battleTypes = listOf(BattleTypes.SINGLES)
    val party: NPCPartyProvider = SimplePartyProvider()
    val alwaysHeal = true
}