/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

@file:Suppress("unused")

package com.cobblemon.mod.common.util.codec

import com.mojang.serialization.Codec

/**
 * Creates a utility type adapter for GSON.
 * This has a few constraints see the type adapter documentation for details.
 *
 * @param T The type of data handled by this [Codec].
 * @return A [CodecTypeAdapter] for the type [T].
 *
 * @see CodecTypeAdapter
 */
fun <T> Codec<T>.toTypeAdapter(): CodecTypeAdapter<T> = CodecTypeAdapter(this)