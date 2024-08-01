/*
 * Copyright (C) 2024 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client

import net.minecraft.network.chat.MutableComponent
import java.util.UUID

class ClientPlayerTeamData {
    var multiBattleTeamMembers = mutableListOf<ClientMultiBattleTeamMember>()
}

class ClientMultiBattleTeamMember(val uuid: UUID, val name: MutableComponent) {
}