package com.cablemc.pokemoncobbled.common.api.serialization

import com.google.gson.JsonElement
import net.minecraft.nbt.NbtElement

/**
 * A serializer for the NBT and Json format.
 *
 * @author Licious
 * @since June 27th, 2022
 */
interface DataSerializer<N : NbtElement, J : JsonElement> {

    /**
     * Loads the given [N] into the object representation.
     *
     * @param nbt The [NbtElement] of type [N] this object is saved as.
     */
    fun loadFromNBT(nbt: N)

    /**
     * Saves the object into the NBT representation.
     *
     * @return The [NbtElement] with the type of [N].
     */
    fun saveToNBT(): N

    /**
     * Loads the given [J] into the object representation.
     *
     * @param json The [JsonElement] of type [J] this object is saved as.
     */
    fun loadFromJson(json: J)

    /**
     * Saves the object into the JSON representation.
     *
     * @return The [JsonElement] with the type of [J].
     */
    fun saveToJson(): J

}