/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.moves.animations

import com.bedrockk.molang.runtime.struct.VariableStruct
import com.cobblemon.mod.common.api.moves.animations.keyframes.ActionEffectKeyframe
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import java.util.concurrent.CompletableFuture

class ActionEffectTimeline(
    val keyframes: MutableList<ActionEffectKeyframe> = mutableListOf(),
) {
    companion object {
        val NONE = ActionEffectTimeline()
    }

    fun run(context: ActionEffectContext): CompletableFuture<Unit> {
        return if (keyframes.isEmpty()) {
            CompletableFuture.completedFuture(Unit)
        } else if (keyframes.size == 1) {
            context.currentKeyframe = keyframes[0]
            keyframes[0].play(context)
        } else {
            context.currentKeyframe = keyframes[0]
            val initial = keyframes[0].play(context)
            // I can never remember the best way to sequence a list of futures
            var finalFuture = initial
            for (i in 1 until keyframes.size) {
                finalFuture = finalFuture.thenApply {
                    val keyframe = keyframes[i]
                    if (context.interrupted) {
                        return@thenApply
                    }
                    context.currentKeyframe = keyframe
                    keyframe.play(context)
                }
            }
            finalFuture
        }
    }
}

class ActionEffectContext(
    val actionEffect: ActionEffectTimeline,
    val userPokemon: PokemonEntity,
    val flags: Set<String>,
    val params: MutableMap<String, Any> = mutableMapOf(),
    val variables: VariableStruct = VariableStruct(),
    var canBeInterrupted: Boolean = false,
    var interrupted: Boolean = false,
    var currentKeyframe: ActionEffectKeyframe? = null
)

//class MoveAnimationKeyframe(
//    val sound: Identifier? = null,
//    val animation: List<AnimationWithMoments> = listOf(),
//    val effects: List<ActionEffect> = listOf()
//)
