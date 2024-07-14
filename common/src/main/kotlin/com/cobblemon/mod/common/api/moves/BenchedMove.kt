/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.moves

import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.registry.CobblemonRegistries
import com.cobblemon.mod.common.util.DataKeys
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.codec.ByteBufCodecs

class BenchedMoves : Iterable<BenchedMove> {
    val observable = SimpleObservable<BenchedMoves>()
    private var emit = true
    private val benchedMoves = mutableListOf<BenchedMove>()

    fun doWithoutEmitting(action: () -> Unit) {
        val previousEmit = emit
        emit = false
        action()
        emit = previousEmit
    }

    fun doThenEmit(action: () -> Unit) {
        doWithoutEmitting(action)
        update()
    }

    fun update() {
        if (emit) {
            observable.emit(this)
        }
    }

    fun add(benchedMove: BenchedMove) = doThenEmit { benchedMoves.add(benchedMove) }
    fun addAll(benchedMoves: Iterable<BenchedMove>) = doThenEmit { this.benchedMoves.addAll(benchedMoves) }
    fun clear() = doThenEmit { benchedMoves.clear() }
    fun remove(benchedMove: BenchedMove) = doThenEmit { benchedMoves.remove(benchedMove) }
    fun remove(moveTemplate: MoveTemplate) = doThenEmit { benchedMoves.removeIf { it.moveTemplate == moveTemplate } }
    override fun iterator() = benchedMoves.iterator()

    companion object {
        @JvmStatic
        val CODEC: Codec<BenchedMoves> = Codec.list(BenchedMove.CODEC)
            .xmap(
                { moveList ->
                    val benchedMoves = BenchedMoves()
                    benchedMoves.addAll(moveList)
                    return@xmap benchedMoves
                },
                BenchedMoves::toList
            )

        @JvmStatic
        val PACKET_CODEC = ByteBufCodecs.fromCodecTrusted(CODEC)
    }
}

data class BenchedMove(val moveTemplate: MoveTemplate, val ppRaisedStages: Int) {

    companion object {
        @JvmStatic
        val CODEC: Codec<BenchedMove> = RecordCodecBuilder.create { it.group(
            CobblemonRegistries.MOVE.byNameCodec().fieldOf(DataKeys.POKEMON_MOVESET_MOVENAME).forGetter(BenchedMove::moveTemplate),
            Codec.intRange(0, 3).fieldOf(DataKeys.POKEMON_MOVESET_RAISED_PP_STAGES).forGetter(BenchedMove::ppRaisedStages)
        ).apply(it, ::BenchedMove) }

        @JvmStatic
        val PACKET_CODEC = ByteBufCodecs.fromCodec(CODEC)
    }
}