package com.cablemc.pokemoncobbled.common.api.storage

import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.api.storage.factory.PokemonStoreFactory
import com.cablemc.pokemoncobbled.common.net.PokemonCobbledNetwork.sendPacket
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

/**
 * The base class for all stores of [Pokemon]. A store specifies the kind of coordinate it needs to be given
 * to use in the generic typing.
 *
 * Saving of a store is not done automatically, and in fact a store has no concept of persistence on its own.
 * You may find [PokemonStoreFactory] instructive, as the factory is responsible for handling a storage's persistence.
 *
 * @author Hiroku
 * @since November 29th, 2021
 */
abstract class PokemonStore<T : StorePosition> : Iterable<Pokemon> {
    /** The UUID of the store. The exact uniqueness requirements depend on the method used for saving. */
    abstract val uuid: UUID
    /** Returns an iterable of all the [Pokemon] in this store, with nulls removed. */
    abstract fun getAll(): Iterable<Pokemon>
    /** Gets the [Pokemon] at the given position. */
    abstract fun get(position: T): Pokemon?
    /** Gets the first empty position that a [Pokemon] might be put. */
    abstract fun getFirstAvailablePosition(): T?
    /** Gets an iterable of all [ServerPlayer]s that should be notified of any changes to the Pokémon in this store. */
    abstract fun getObservingPlayers(): Iterable<ServerPlayer>
    /** Sends the contents of this store to a player as if they've never seen it before. This initializes the store then sends each contained Pokémon. */
    abstract fun sendTo(player: ServerPlayer)
    /**
     * Iterates over all the Pokémon in this store and sets their store coordinates. This might not be necessary
     * depending on the save method being used, this should be called internally when a store is first loaded.
     *
     * If this does not get called, or it does not do its job properly, serious de-sync issues may follow.
     */
    abstract fun setupStoreCoordinates()

    /**
     * Sets the given position with the given [Pokemon], which can be null. This is for internal use only because
     * other, more public methods will additionally send updates to the client, and for logical reasons this means
     * there must be an internal and external set method.
     */
    protected abstract fun setAtPosition(position: T, pokemon: Pokemon?)

    /** Sends the given packet to all observing players. */
    open fun sendPacketToObservers(packet: NetworkPacket) = getObservingPlayers().forEach { it.sendPacket(packet) }

    /** Adds the given [Pokemon] to the first available space. Returns false if there is no space. */
    open fun add(pokemon: Pokemon): Boolean {
        val position = getFirstAvailablePosition() ?: return false // Couldn't fit, shrug emoji
        set(position, pokemon)
        return true
    }

    /**
     * Sets the specified position to the specified [Pokemon]. If there is already a Pokémon in that slot,
     * it will be removed from the store entirely.
     *
     * This method will also notify any observing players about the changes.
     */
    open fun set(position: T, pokemon: Pokemon) {
        val existing = get(position)
        if (existing == pokemon) {
            return
        }

        if (existing != pokemon && existing != null) {
            remove(existing)
        }

        setAtPosition(position, pokemon)
    }

    /** Swaps the Pokémon at the specified positions. If one of the spaces is empty, it will simply move the not-null one to that space. */
    open fun swap(position1: T, position2: T) {
        val pokemon1 = get(position1)
        val pokemon2 = get(position2)
        setAtPosition(position1, pokemon2)
        setAtPosition(position2, pokemon1)
    }

    /**
     * Moves the specified [Pokemon] to the specified space. This will do nothing if the Pokémon is not in this store.
     *
     * This is a shortcut to running [PokemonStore.swap]
     */
    fun move(pokemon: Pokemon, position: T) {
        val currentPosition = pokemon.storeCoordinates.get() ?: return
        if (currentPosition.store != this) {
            return
        }
        swap(currentPosition.position as T, position)
    }

    /** Removes any Pokémon that may be at the specified spot. Returns true if there was a Pokémon to remove. */
    open fun remove(position: T): Boolean {
        val pokemon = get(position)
        return if (pokemon == null) {
            false
        } else {
            return remove(pokemon)
        }
    }

    /** Removes the specified Pokémon from this store. Returns true if the Pokémon was in this store and was successfully removed. */
    open fun remove(pokemon: Pokemon): Boolean {
        val currentPosition = pokemon.storeCoordinates.get()?.let { it as StoreCoordinates<T> } ?: return false
        if (currentPosition.store != this || get(currentPosition.position) != pokemon) { // Whacky synchronization issue?
            return false
        }
        setAtPosition(currentPosition.position, null)
        return true
    }
}