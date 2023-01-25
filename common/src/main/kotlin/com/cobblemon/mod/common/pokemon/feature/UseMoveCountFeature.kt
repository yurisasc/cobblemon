/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.feature

import com.cobblemon.mod.common.api.moves.MoveTemplate
import com.cobblemon.mod.common.api.moves.Moves
import com.cobblemon.mod.common.api.pokemon.feature.SpeciesFeature
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList

/**
 * A feature that keeps track of the amount of times a specific move hits.
 *
 * @author Licious
 * @since January 25th, 2023
 */
class UseMoveCountFeature : SpeciesFeature {

    override val name: String = ID

    private val moves = hashMapOf<MoveTemplate, Int>()

    /**
     * Checks the amount of times the given [move] has been used.
     *
     * @param move The [MoveTemplate] being queried.
     * @return The amount of times the move has been used.
     */
    fun amount(move: MoveTemplate) = this.moves[move] ?: 0

    /**
     * Increments the usage count for the given [move].
     *
     * @param move The [MoveTemplate] being incremented.
     */
    fun increment(move: MoveTemplate) {
        this.moves[move] = (this.moves[move] ?: 0) + 1
    }

    override fun saveToNBT(pokemonNBT: NbtCompound): NbtCompound {
        val list = NbtList()
        this.moves.forEach { (move, amount) ->
            val nbt = NbtCompound()
            nbt.putString(MOVE, move.name)
            nbt.putInt(AMOUNT, amount)
            list.add(nbt)
        }
        pokemonNBT.put(ID, list)
        return pokemonNBT
    }

    override fun loadFromNBT(pokemonNBT: NbtCompound): SpeciesFeature {
        this.moves.clear()
        val list = pokemonNBT.getList(ID, NbtElement.COMPOUND_TYPE.toInt())
        list.forEach { element ->
            val nbt = (element as NbtCompound)
            this.tryLoad(nbt.getString(MOVE), nbt.getInt(AMOUNT))
        }
        return this
    }

    override fun saveToJSON(pokemonJSON: JsonObject): JsonObject {
        val jArray = JsonArray()
        this.moves.forEach { (move, amount) ->
            val jObject = JsonObject()
            jObject.addProperty(MOVE, move.name)
            jObject.addProperty(AMOUNT, amount)
            jArray.add(jObject)
        }
        pokemonJSON.add(ID, jArray)
        return pokemonJSON
    }

    override fun loadFromJSON(pokemonJSON: JsonObject): SpeciesFeature {
        this.moves.clear()
        val jArray = pokemonJSON.getAsJsonArray(ID)
        jArray.forEach { element ->
            val jObject = element.asJsonObject
            this.tryLoad(jObject.get(MOVE).asString, jObject.get(AMOUNT).asInt)
        }
        return this
    }

    private fun tryLoad(rawMove: String, amount: Int) {
        val move = Moves.getByName(rawMove) ?: return
        this.moves[move] = amount.coerceAtLeast(0)
    }

    companion object {
        const val ID = "use_move"
        private const val MOVE = "move"
        private const val AMOUNT = "amount"
    }

}