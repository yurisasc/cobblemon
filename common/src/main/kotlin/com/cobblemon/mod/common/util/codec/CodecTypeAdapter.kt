/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.util.codec

import com.cobblemon.mod.common.Cobblemon
import com.google.gson.*
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import java.lang.reflect.Type

/**
 * A simple way to convert a Codec to a valid GSON (de)serializer.
 * This will not use any other adapters from the operation context as such the codec is expected to handle all inner types.
 *
 * @param T The type of resulting data.
 * @property codec The [Codec] responsible for all operations.
 * @throws NoSuchElementException If the operations are impossible, this is due to the optional encapsulation.
 */
class CodecTypeAdapter<T>(val codec: Codec<T>) : JsonDeserializer<T>, JsonSerializer<T> {

    override fun deserialize(jElement: JsonElement, type: Type, ctx: JsonDeserializationContext): T = this.codec.parse(JsonOps.INSTANCE, jElement).resultOrPartial(Cobblemon.LOGGER::error).orElseThrow()

    override fun serialize(instance: T, type: Type, ctx: JsonSerializationContext): JsonElement = this.codec.encodeStart(JsonOps.INSTANCE, instance).resultOrPartial(Cobblemon.LOGGER::error).orElseThrow()


}