/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common

import com.cobblemon.mod.common.item.components.HeldItemCapableComponent
import com.cobblemon.mod.common.item.components.PokemonItemComponent
import com.cobblemon.mod.common.item.RodBaitComponent
import com.cobblemon.mod.common.platform.PlatformRegistry
import net.minecraft.component.DataComponentType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier

object CobblemonItemComponents : PlatformRegistry<Registry<DataComponentType<*>>, RegistryKey<Registry<DataComponentType<*>>>, DataComponentType<*>>() {

    val POKEMON_ITEM: DataComponentType<PokemonItemComponent> = DataComponentType.builder<PokemonItemComponent>()
        .codec(PokemonItemComponent.CODEC)
        .packetCodec(PokemonItemComponent.PACKET_CODEC)
        .build()

    val HELD_ITEM_REP: DataComponentType<HeldItemCapableComponent> = DataComponentType.builder<HeldItemCapableComponent>()
        .codec(HeldItemCapableComponent.CODEC)
        .packetCodec(HeldItemCapableComponent.PACKET_CODEC)
        .build()

    val BAIT: DataComponentType<RodBaitComponent> = DataComponentType.builder<RodBaitComponent>()
        .codec(RodBaitComponent.CODEC)
        .packetCodec(RodBaitComponent.PACKET_CODEC)
        .build()



    fun register() {
        Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier("cobblemon:pokemon_item"), POKEMON_ITEM)
        Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier("cobblemon:bait"), BAIT)
    }

    override val registry = Registries.DATA_COMPONENT_TYPE
    override val registryKey = RegistryKeys.DATA_COMPONENT_TYPE

}