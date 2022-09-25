/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common

import com.cablemc.pokemoncobbled.common.registry.CompletableRegistry
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.sound.SoundEvent
import net.minecraft.util.registry.Registry

object CobbledSounds : CompletableRegistry<SoundEvent>(Registry.SOUND_EVENT_KEY) {
    private fun queue(name: String) = queue(name) { SoundEvent(cobbledResource(name)) }

    val CAPTURE_SUCCEEDED = queue("capture_succeeded")
    val POKEBALL_SHAKE = queue("shake")
    val POKEBALL_HIT = queue("hit")
    val SEND_OUT = queue("send_out")
    val RECALL = queue("recall")
    val CAPTURE_STARTED = queue("capture_started")
}