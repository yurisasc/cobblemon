package com.cablemc.pokemoncobbled.common.api.data

import com.cablemc.pokemoncobbled.common.api.reactive.SimpleObservable
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourceType
import net.minecraft.util.Identifier
import java.nio.file.Path

/**
 * A registry with data provided by a resource or data pack.
 *
 * @param T The type of this registry
 *
 * @author Licious
 * @since August 1st, 2022
 */
interface DataRegistry<T> {

    /**
     * The unique [Identifier] of this registry.
     */
    val id: Identifier

    /**
     * The expected [ResourceType].
     */
    val type: ResourceType

    /**
     * The [Path] for the data this registry will consume.
     */
    val resourcePath: Path

    /**
     * An observable that emits whenever this registry has finished reloading.
     */
    val observable: SimpleObservable<T>

    /**
     * Reloads this registry.
     *
     * @param manager The newly updated [ResourceManager]
     */
    fun reload(manager: ResourceManager)

    /*
    fun registerDataListener() {
        ReloadListenerRegistry.register(this.type, this.reloader)
        PokemonCobbled.LOGGER.info("Registered {} DataRegistry", this.id.toString())
    }
     */

}