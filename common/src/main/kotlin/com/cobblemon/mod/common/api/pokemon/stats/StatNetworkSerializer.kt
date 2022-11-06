/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.pokemon.stats

import com.cobblemon.mod.common.api.net.ContextedDecodable
import com.cobblemon.mod.common.api.net.ContextedEncodable
import com.cobblemon.mod.common.pokemon.stat.CobblemonStatNetworkSerializer
import net.minecraft.network.PacketByteBuf

/**
 * Responsible for decoding and encoding stats from and to a [PacketByteBuf].
 * The base implementation can be found in [CobblemonStatNetworkSerializer].
 *
 * @author Licious
 * @since November 6th, 2022
 */
interface StatNetworkSerializer: ContextedEncodable<Stat>, ContextedDecodable<Stat>