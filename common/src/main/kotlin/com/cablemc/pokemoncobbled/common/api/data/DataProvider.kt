package com.cablemc.pokemoncobbled.common.api.data

/**
 * Provides a general listener for resource and data pack updates notifying the [DataRegistry] listening.
 *
 * @author Licious
 * @since August 5th, 2022
 */
interface DataProvider {

    /**
     * Registers a [DataRegistry] to listen for updates.
     * The updates will automatically happen on the correct sides based on [DataRegistry.type].
     *
     * @param registry The [DataRegistry] being registered.
     */
    fun register(registry: DataRegistry<*>)

}