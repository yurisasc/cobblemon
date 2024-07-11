/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.effect.types

import com.cobblemon.mod.common.api.data.ShowdownIdentifiable
import com.cobblemon.mod.common.api.effect.Effect
import com.cobblemon.mod.common.api.effect.EffectType
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import java.util.function.Function

class ShowdownConditionEffect(private val conditionId: String) : Effect() {

    init {
        if (ShowdownIdentifiable.REGEX.matches(this.conditionId)) {
            throw IllegalArgumentException("${this.conditionId} does not match regex ${ShowdownIdentifiable.REGEX}")
        }
    }

    override fun type(): EffectType<*> = EffectType.SHOWDOWN_CONDITION

    override fun showdownId(): String = this.conditionId

    companion object {
        @JvmStatic
        val CODEC: MapCodec<ShowdownConditionEffect> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Codec.STRING.comapFlatMap(
                    { string ->
                        if (ShowdownIdentifiable.REGEX.matches(string)) {
                            DataResult.success(string)
                        } else {
                            DataResult.error { "$string does not match regex ${ShowdownIdentifiable.REGEX}" }
                        }
                    },
                    Function.identity()
                ).fieldOf("conditionId").forGetter(ShowdownConditionEffect::conditionId)
            ).apply(instance, ::ShowdownConditionEffect)
        }
    }
}