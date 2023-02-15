/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common

import com.cobblemon.mod.common.platform.PlatformRegistry
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.sound.SoundEvent

object CobblemonSounds : PlatformRegistry<Registry<SoundEvent>, RegistryKey<Registry<SoundEvent>>, SoundEvent>() {

    override val registry: Registry<SoundEvent> = Registries.SOUND_EVENT
    override val registryKey: RegistryKey<Registry<SoundEvent>> = RegistryKeys.SOUND_EVENT

    val GUI_CLICK = this.create("gui.click")

    val PC_ON = this.create("pc.on")
    val PC_OFF = this.create("pc.off")
    val PC_GRAB = this.create("pc.grab")
    val PC_DROP = this.create("pc.drop")
    val PC_RELEASE = this.create("pc.release")

    val HEALING_MACHINE_ACTIVE = this.create("healing_machine.active")

    val POKE_BALL_CAPTURE_STARTED = this.create("poke_ball.capture_started")
    val POKE_BALL_CAPTURE_SUCCEEDED = this.create("poke_ball.capture_succeeded")
    val POKE_BALL_SHAKE = this.create("poke_ball.shake")
    val POKE_BALL_OPEN = this.create("poke_ball.open")
    val POKE_BALL_HIT = this.create("poke_ball.hit")
    val POKE_BALL_SEND_OUT = this.create("poke_ball.send_out")
    val POKE_BALL_RECALL = this.create("poke_ball.recall")

    val ITEM_USE = this.create("item.use")
    
    private fun create(name: String): SoundEvent = this.create(name, SoundEvent.of(cobblemonResource(name)))
    

}