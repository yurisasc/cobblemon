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