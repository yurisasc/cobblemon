/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.conditional

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import net.minecraft.registry.Registry
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier

/**
 * A condition which applies to an entry in some [Registry].
 *
 * @author Hiroku
 * @since July 16th, 2022
 */
sealed interface RegistryLikeCondition<T> {

    /**
     * Checks if a given [value] matches this condition.
     *
     * @param value The value being matched against this condition.
     * @param registry The [Registry] of type [T] being queried.
     * @return If the condition is fulfilled.
     */
    fun fits(value: T, registry: Registry<T>): Boolean

    /**
     * Generates a string like human-friendly representation used by the Codec when serializing.
     *
     * @return The string like human-friendly representation.
     */
    fun pretty(): String

    companion object {

        @JvmStatic
        fun <T> createCodec(registryAccessor: () -> Registry<T>): Codec<RegistryLikeCondition<T>> = Codec.STRING.comapFlatMap(
            { string ->
                val registry = registryAccessor.invoke()
                val isTag = string.startsWith("#")
                val rawIdentifier = if (isTag) string.substringAfter("#") else string
                val identifier = Identifier.tryParse(rawIdentifier) ?: return@comapFlatMap DataResult.error { "Cannot resolve identifier/tag from the string $string" }
                if (isTag) {
                    val tag = TagKey.of(registry.key, identifier)
                    return@comapFlatMap DataResult.success(RegistryTagCondition(tag))
                }
                val element = registry.get(identifier) ?: return@comapFlatMap DataResult.error { "Cannot find an element in registry ${registry.key.value} with the ID $identifier" }
                return@comapFlatMap DataResult.success(RegistryElementCondition(element, identifier))
            },
            { it.pretty() }
        )

    }

}