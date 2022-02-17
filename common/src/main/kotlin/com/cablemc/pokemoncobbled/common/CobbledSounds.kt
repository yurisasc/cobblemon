package com.cablemc.pokemoncobbled.common

import com.cablemc.pokemoncobbled.common.PokemonCobbled.MODID
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import dev.architectury.registry.registries.DeferredRegister
import net.minecraft.core.Registry
import net.minecraft.sounds.SoundEvent

object CobbledSounds {
    private val soundRegister = DeferredRegister.create(MODID, Registry.SOUND_EVENT_REGISTRY)
    private fun queue(name: String) = soundRegister.register(name) { SoundEvent(cobbledResource(name.replace("_", "."))) }

    val CAPTURE_SUCCEEDED = queue("capture_succeeded")
    val POKEBALL_SHAKE = queue("shake")
    val POKEBALL_HIT = queue("hit")
    val SEND_OUT = queue("send_out")
    val RECALL = queue("recall")
    val CAPTURE_STARTED = queue("capture_started")

    fun register() {
        soundRegister.register()
    }
}