/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.music

import com.cobblemon.mod.common.CobblemonSounds
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.sound.MusicSound
import net.minecraft.sound.SoundEvent


object CustomMusic {
    private fun createCustomIngameMusic(sound: RegistryEntry<SoundEvent?>?): MusicSound {
        return MusicSound(sound, 12000, 24000, false)
    }

    @JvmField
    val DEEP_DARK = createCustomIngameMusic(RegistryEntry.of(CobblemonSounds.MUSIC_DEEP_DARK))
}