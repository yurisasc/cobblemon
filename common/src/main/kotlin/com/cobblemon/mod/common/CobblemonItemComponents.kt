package com.cobblemon.mod.common

import com.cobblemon.mod.common.item.PokemonItemComponent
import net.minecraft.component.DataComponentType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object CobblemonItemComponents {

    val POKEMON_ITEM: DataComponentType<PokemonItemComponent> = DataComponentType.builder<PokemonItemComponent>()
        .codec(PokemonItemComponent.CODEC)
        .packetCodec(PokemonItemComponent.PACKET_CODEC)
        .build()

    fun register() {
        Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier("cobblemon:pokemon_item"), POKEMON_ITEM)
    }

}