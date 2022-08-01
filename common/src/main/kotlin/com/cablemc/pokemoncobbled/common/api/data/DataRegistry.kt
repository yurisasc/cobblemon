package com.cablemc.pokemoncobbled.common.api.data

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import dev.architectury.registry.ReloadListenerRegistry
import net.minecraft.resource.ResourceReloader
import net.minecraft.resource.ResourceType
import net.minecraft.util.Identifier

/**
 * A registry with a [ResourceReloader] attached to it.
 *
 * @author Licious
 * @since August 1st, 2022
 */
interface DataRegistry {

    /**
     * The unique [Identifier] of this registry.
     */
    val id: Identifier

    /**
     * The expected [ResourceType].
     */
    val type: ResourceType

    /**
     * The [ResourceReloader] powering this registry.
     */
    val reloader: ResourceReloader

    /**
     * Registers this registry using the platform appropriate methods.
     *
     */
    fun registerDataListener() {
        ReloadListenerRegistry.register(this.type, this.reloader)
        PokemonCobbled.LOGGER.info("Registered {} DataRegistry", this.id.toString())
    }

}