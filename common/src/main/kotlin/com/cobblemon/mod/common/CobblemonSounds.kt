/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common

import com.cobblemon.mod.common.registry.CompletableRegistry
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.sound.SoundEvent
import net.minecraft.util.registry.Registry

object CobblemonSounds : CompletableRegistry<SoundEvent>(Registry.SOUND_EVENT_KEY) {
    private fun queue(name: String) = queue(name) { SoundEvent(cobblemonResource(name)) }

    val CAPTURE_SUCCEEDED = queue("capture_succeeded")
    val POKEBALL_SHAKE = queue("shake")
    val POKEBALL_HIT = queue("hit")
    val SEND_OUT = queue("send_out")
    val RECALL = queue("recall")
    val CAPTURE_STARTED = queue("capture_started")
}