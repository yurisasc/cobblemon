package com.cablemc.pokemoncobbled.common.api.registry

import net.minecraft.resources.ResourceLocation

/**
 * A base implementation of [Registry].
 *
 * @author Licious
 * @since March 2nd, 2022
 */
abstract class RegistryBase<T : Keyed> : Registry<T> {

    private val registered = hashMapOf<ResourceLocation, T>()

    override fun register(item: T): T {
        this.registered[item.id] = item
        return item
    }

    override fun get(key: ResourceLocation): T? = this.registered[key]

    override fun random(): T? = this.registered.values.randomOrNull()

}
