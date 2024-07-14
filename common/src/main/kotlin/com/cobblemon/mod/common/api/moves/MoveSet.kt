/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.moves

import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.net.IntSize
import com.cobblemon.mod.common.util.readSizedInt
import com.cobblemon.mod.common.util.writeSizedInt
import net.minecraft.network.RegistryFriendlyByteBuf
import com.mojang.serialization.Codec
import net.minecraft.network.codec.ByteBufCodecs
import kotlin.math.min

class MoveSet : Iterable<Move> {
    val observable = SimpleObservable<MoveSet>()
    private var emit = true

    private val moves = arrayOfNulls<Move>(MOVE_COUNT)


    override fun iterator() = moves.filterNotNull().iterator()

    operator fun get(index: Int) = index.takeIf { it in 0 until MOVE_COUNT }?.let { moves[it] }

    /**
     * Gets all Moves from the Pokémon but skips null Moves
     */
    fun getMoves(): List<Move> {
        return moves.filterNotNull()
    }

    fun hasSpace() = moves.any { it == null }

    /**
     * Sets the given Move to given position
     */
    fun setMove(pos: Int, move: Move?) {
        if (pos !in 0 until MOVE_COUNT) {
            return
        }
        moves[pos] = move
        move?.observable?.subscribe { this.update() }
        update()
    }

    fun copyFrom(other: MoveSet) {
        doWithoutEmitting {
            clear()
            other.getMoves().forEach { add(it) }
        }
        update()
    }

    fun heal() {
        getMoves().forEach { it.currentPp = it.maxPp }
        update()
    }

    fun partialHeal() {
        getMoves().forEach { it.currentPp = min((it.currentPp + (it.maxPp / 2)), it.maxPp)}
        update()
    }

    fun clear() {
        doWithoutEmitting {
            for (i in 0 until MOVE_COUNT){
                setMove(i, null)
            }
        }
        update()
    }

    /**
     * Swaps the position of the two given Moves indices
     */
    fun swapMove(pos1: Int, pos2: Int) {
        // The fact that this works should be a fuckin crime wth
        moves[pos1] = moves[pos2].also {
            moves[pos2] = moves[pos1]
        }
        update()
    }

    fun add(move: Move) {
        if (any { it.template == move.template }) {
            return
        }
        for (i in 0 until MOVE_COUNT) {
            if (moves[i] == null) {
                moves[i] = move
                update()
                return
            }
        }
    }

    fun update() {
        if (emit) {
            observable.emit(this)
        }
    }

    fun doWithoutEmitting(action: () -> Unit) {
        val previousEmit = emit
        emit = false
        action()
        emit = previousEmit
    }

    companion object {
        const val MOVE_COUNT = 4
        @JvmStatic
        val CODEC: Codec<MoveSet> = Codec.list(Move.CODEC, 0, MOVE_COUNT)
            .xmap(
                { moveList ->
                    val moveSet = MoveSet()
                    moveList.forEach(moveSet::add)
                    return@xmap moveSet
                },
                { moveSet -> moveSet.toList() }
            )

        @JvmStatic
        val PACKET_CODEC = ByteBufCodecs.fromCodecTrusted(CODEC)
    }
}