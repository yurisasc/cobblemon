package com.cablemc.pokemoncobbled.common.sound

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.sounds.SoundEvent
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object SoundRegistry {
    private val SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, PokemonCobbled.MODID)

    val CAPTURE_SUCCEEDED = registerSound("capture_succeeded")
    val POKEBALL_SHAKE = registerSound("shake")
    val POKEBALL_HIT = registerSound("hit")
    val SEND_OUT = registerSound("send_out")
    val RECALL = registerSound("recall")
    val CAPTURE_STARTED = registerSound("capture_started")

    private fun registerSound(name: String): RegistryObject<SoundEvent> = registerSound(name, SoundEvent(cobbledResource(name)))

    private fun registerSound(
        name: String,
        sound: SoundEvent
    ): RegistryObject<SoundEvent> {
        return SOUNDS.register(name) { sound }
    }

    fun register(bus: IEventBus) {
        SOUNDS.register(bus)
    }
}