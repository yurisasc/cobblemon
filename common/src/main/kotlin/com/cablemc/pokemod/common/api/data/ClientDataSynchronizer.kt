package com.cablemc.pokemod.common.api.data

import com.cablemc.pokemod.common.api.net.Decodable
import com.cablemc.pokemod.common.api.net.Encodable

/**
 * Represents an object that will possibly be synchronized to the client during datapack synchronization.
 *
 * @param T The type of the object being synchronized.
 *
 * @author Licious
 * @since October 14th, 2022
 */
interface ClientDataSynchronizer<T> : Decodable, Encodable {

    /**
     * Compares an object of type [T] with this instance to see if synchronization is necessary.
     *
     * @param other The other instance of type [T].
     * @return If synchronization is necessary.
     */
    fun shouldSynchronize(other: T): Boolean

}