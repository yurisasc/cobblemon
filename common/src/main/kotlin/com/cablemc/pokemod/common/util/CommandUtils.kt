/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.util

import com.mojang.brigadier.builder.ArgumentBuilder
import net.minecraft.server.command.ServerCommandSource

fun <T : ArgumentBuilder<ServerCommandSource, T>> ArgumentBuilder<ServerCommandSource, T>.appendRequirement(requirement: (src: ServerCommandSource) -> Boolean): T = this.requires(this.requirement.and(requirement))