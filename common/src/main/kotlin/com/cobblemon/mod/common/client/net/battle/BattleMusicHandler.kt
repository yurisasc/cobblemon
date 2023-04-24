/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.net.battle

import com.cobblemon.mod.common.Cobblemon.LOGGER
import com.cobblemon.mod.common.access.SoundManagerDuck
import com.cobblemon.mod.common.api.net.ClientNetworkPacketHandler
import com.cobblemon.mod.common.mixin.accessor.MusicTrackerAccessor
import com.cobblemon.mod.common.net.messages.client.battle.BattleMusicPacket
import net.minecraft.client.MinecraftClient
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.sound.SoundInstance
import net.minecraft.client.sound.SoundManager
import net.minecraft.client.sound.TickableSoundInstance
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper

object BattleMusicHandler : ClientNetworkPacketHandler<BattleMusicPacket> {

    // Keep a reference for look-up in SoundManager
    var music: BattleMusicInstance? = null
    // SoundCategories that are blocked while battle music is playing
    private val interferences = listOf(SoundCategory.AMBIENT, SoundCategory.RECORDS)

    override fun handle(packet: BattleMusicPacket, client: MinecraftClient) {
        val soundManager = client.soundManager
        val newMusic = packet.music
        val currMusic = this.music

        if (newMusic == null)
            soundManager.endMusic()
        else if (currMusic == null)
            soundManager.initializeMusic(newMusic, packet.volume, packet.pitch)
        else if (newMusic.id != currMusic.id)
            soundManager.switchMusic(newMusic, packet.volume, packet.pitch)
        else
            LOGGER.error("Ignored BattleMusicPacket from server: ${packet.music?.id}")
    }

    // stop all interference sounds currently playing and start a new BattleMusicInstance
    private fun SoundManager.initializeMusic(sound: SoundEvent, volume: Float, pitch: Float) {
        this.stopSounds(null, SoundCategory.MUSIC)
        interferences.forEach { this.stopSounds(null, it) }
        (this as SoundManagerDuck).toggleCategories(*interferences.toTypedArray())
        music = BattleMusicInstance(sound, volume, pitch)
        this.play(music)
    }

    // switch (without fade) to a new BattleMusicInstance if one is already playing
    private fun SoundManager.switchMusic(sound: SoundEvent, volume: Float, pitch: Float) {
        this.stop(music)
        music = BattleMusicInstance(sound, volume, pitch)
        this.play(music)
    }

    // fade out and end current BattleMusicInstance
    private fun SoundManager.endMusic() {
        (this as SoundManagerDuck).toggleCategories(*interferences.toTypedArray())
        music?.setFade()
        music = null
    }
}

class BattleMusicInstance(sound: SoundEvent, volume: Float, pitch: Float) :
        PositionedSoundInstance(sound, SoundCategory.MUSIC, volume, pitch, SoundInstance.createRandom(), BlockPos.ORIGIN), TickableSoundInstance {

    private var fade: Boolean = false
    private var done: Boolean = false
    private val tracker: MusicTrackerAccessor
    private var tickCount = 0
    private var fadeCount = 0
    private val fadeTime = 60.0
    private var initVolume = 1.0

    init {
        this.relative = true
        this.repeat = true
        this.attenuationType = SoundInstance.AttenuationType.NONE
        this.tracker = MinecraftClient.getInstance().musicTracker as MusicTrackerAccessor
        this.initVolume = volume.toDouble()
    }

    override fun isDone(): Boolean {
        return this.done
    }

    fun setFade() {
        this.fade = true
        this.repeat = false
    }

    override fun tick() {
        ++tickCount
        if (fade) {
            ++fadeCount
            this.volume = MathHelper.lerp(fadeCount/fadeTime, initVolume, 0.0).toFloat()
            if (volume <= 0) this.done = true
        }
        if (tickCount % 20 == 0) tracker.setTimeUntilNextSong(tracker.timeUntilNextSong() + 20)
    }
}