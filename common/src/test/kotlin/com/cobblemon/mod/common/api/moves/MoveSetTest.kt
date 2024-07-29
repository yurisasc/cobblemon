/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.moves

import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import com.google.gson.JsonObject

object DataKeys {
    const val POKEMON_MOVESET = "pokemon_moveset_"
}

internal class MoveSetTest {
    @Test
    fun `it should swap two moves properly`() {
        val moveset = MoveSet()
        val move1 = Move( mockk(), 11, 2,)
        val move2 = Move(mockk(), 10, 4)
        moveset.setMove(0, move1)
        moveset.setMove(1, move2)
        moveset.swapMove(0, 1)
        assertEquals(move1, moveset[1])
        assertEquals(move2, moveset[0])
    }
 @Test
    fun `it should not set a move if position is negative`() {
        val moveset = MoveSet()
        val move = Move(mockk(), 11, 2)
        moveset.setMove(-1, move)
        assertNull(moveset[-1])
    }

    @Test
    fun `it should not set a move if position is greater than move count`() {
        val moveset = MoveSet()
        val move = Move(mockk(), 11, 2)
        moveset.setMove(MoveSet.MOVE_COUNT, move)
        assertNull(moveset[MoveSet.MOVE_COUNT])
    }

    @Test
    fun `it should add a move when position is valid`() {
        val moveset = MoveSet()
        val move = Move(mockk(), 11, 2)
        moveset.setMove(1, move)
        assertEquals(move, moveset[1])
    }

    @Test
    fun `it should not add a move if already exists`() {
        val moveset = MoveSet()
        val move = Move(mockk(), 11, 2)
        moveset.setMove(1, move)
        moveset.add(move)
        assertEquals(1, moveset.getMoves().size)
    }

    @Test
    fun `it should add a move if not already exists`() {
        val moveset = MoveSet()
        val move1 = Move(mockk(), 11, 2)
        val move2 = Move(mockk(), 10, 4)
        moveset.setMove(1, move1)
        moveset.add(move2)
        assertEquals(2, moveset.getMoves().size)
    }

    @Test
    fun `it should update observable if emit is true`() {
        val moveset = MoveSet()
        var updated = false
        moveset.observable.subscribe { updated = true }
        moveset.update()
        assertTrue(updated)
    }

    @Test
    fun `it should not update observable if emit is false`() {
        val moveset = MoveSet()
        var updated = false
        moveset.observable.subscribe { updated = true }
        moveset.doWithoutEmitting {
            moveset.update()
        }
        assertFalse(updated)
    }

    @Test
    fun `it should continue if json entry is null`() {
        val moveset = MoveSet()
        val json = com.google.gson.JsonObject() // Assuming the use of Gson for JsonObject
        for (i in 0 until MoveSet.MOVE_COUNT) {
            json.add(DataKeys.POKEMON_MOVESET + i, null)
        }
        moveset.loadFromJSON(json)
        assertTrue(moveset.getMoves().isEmpty())
    }

    @Test
    fun `it should add move if json entry is not null`() {
        val moveset = MoveSet()
        val json = com.google.gson.JsonObject() // Assuming the use of Gson for JsonObject
        val moveJson = com.google.gson.JsonObject() // Assuming the use of Gson for JsonObject
        moveJson.addProperty("template", "move_template")
        json.add(DataKeys.POKEMON_MOVESET + 0, moveJson)
        moveset.loadFromJSON(json)
        assertEquals(1, moveset.getMoves().size)
    }
}