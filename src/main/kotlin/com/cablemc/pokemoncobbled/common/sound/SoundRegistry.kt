package com.cablemc.pokemoncobbled.common.sound

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.sounds.SoundEvent
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fmllegacy.RegistryObject
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

object SoundRegistry {
    private val SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, PokemonCobbled.MODID)

    val CAPTURE_SUCCEEDED = registerSound("capture_succeeded", SoundEvent(cobbledResource("capture_succeeded")))
    val POKEBALL_SHAKE = registerSound("shake", SoundEvent(cobbledResource("shake")))

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