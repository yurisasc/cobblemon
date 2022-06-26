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