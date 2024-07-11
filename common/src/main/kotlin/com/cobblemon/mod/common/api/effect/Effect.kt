/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.effect

import com.cobblemon.mod.common.api.data.ShowdownIdentifiable
import com.mojang.serialization.Codec

abstract class Effect : ShowdownIdentifiable {

    abstract fun type(): EffectType<*>

    companion object {

        @JvmStatic
        val CODEC: Codec<Effect> = EffectType.REGISTRY
            .byNameCodec()
            .dispatch(Effect::type) { it.codec() }

    }

}