package com.cablemc.pokemoncobbled.common.api.registry

import net.minecraft.resources.ResourceLocation

/**
 * Holds [Keyed] attributes.
 *
 * @author Licious
 * @since March 2nd, 2022
 */
interface Registry<T : Keyed> {

    /**
     * Register the given item to the registry.
     * If [Keyed.id] is present in any of the existing items the old item will be replaced.
     *
     * @param item The item being registered.
     * @return The newly registered item.
     */
    fun register(item: T): T

    /**
     * Obtains a registered item if existing.
     *
     * @param key The [ResourceLocation] of the [Keyed] item.
     * @return The item if existing.
     */
    fun get(key: ResourceLocation): T?

    /**
     * Returns a random item from the registry.
     *
     * @return A random item or null if the registry is empty.
     */
    fun random(): T?

}
