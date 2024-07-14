/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.moves

import com.cobblemon.mod.common.api.moves.categories.DamageCategory
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.api.types.ElementalType
import com.cobblemon.mod.common.registry.CobblemonRegistries
import com.cobblemon.mod.common.util.DataKeys
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import kotlin.math.ceil
import kotlin.properties.Delegates

/**
 * Representing a Move based on some template and with current PP and the number of raised PP stages.
 */
open class Move(
    val template: MoveTemplate,
    currentPp: Int,
    raisedPpStages: Int = 0
) {
    private var emit = true
    val observable = SimpleObservable<Move>()

    var currentPp: Int by Delegates.observable(currentPp) { _, oldValue, newValue ->
        if (oldValue != newValue) {
            update()
        }
    }

    var raisedPpStages: Int by Delegates.observable(raisedPpStages) { _, oldValue, newValue ->
        if (oldValue != newValue) {
            update()
        }
    }

    fun doThenUpdate(action: () -> Unit) {
        val oldEmit = emit
        emit = false
        action()
        emit = oldEmit
        update()
    }

    fun update() {
        if (emit) {
            observable.emit(this)
        }
    }

    val displayName: Component
        get() = template.displayName

    val description: Component
        get() = template.description

    val type: ElementalType
        get() = template.elementalType

    val damageCategory: DamageCategory
        get() = template.damageCategory

    val power: Double
        get() = template.power

    val accuracy: Double
        get() = template.accuracy

    val effectChances: Array<Double>
        get() = template.effectChances

    val maxPp: Int
        get() = template.pp + raisedPpStages * template.pp / 5

    /** Raises the max PP and rescales the current PP value. Returns false if it was already maxed out. */
    fun raiseMaxPP(amount: Int): Boolean {
        val oldPp = maxPp
        doThenUpdate {
            val ppRatio = currentPp / maxPp.toFloat()
            raisedPpStages += amount
            if (raisedPpStages > 3) {
                raisedPpStages = 3
            }
            currentPp = ceil(ppRatio * maxPp).toInt()
        }
        return oldPp != maxPp
    }

    companion object {
        @JvmStatic
        val CODEC: Codec<Move> = RecordCodecBuilder.create { it.group(
            CobblemonRegistries.MOVE.byNameCodec().fieldOf(DataKeys.POKEMON_MOVESET_MOVENAME).forGetter(Move::template),
            Codec.intRange(0, Int.MAX_VALUE).fieldOf(DataKeys.POKEMON_MOVESET_MOVEPP).forGetter(Move::currentPp),
            Codec.intRange(0, 3).fieldOf(DataKeys.POKEMON_MOVESET_RAISED_PP_STAGES).forGetter(Move::raisedPpStages)
        ).apply(it) { template, currentPp, raisedPpStages -> template.create(currentPp, raisedPpStages) } }

        @JvmStatic
        val PACKET_CODEC = ByteBufCodecs.fromCodecTrusted(CODEC)
    }
}