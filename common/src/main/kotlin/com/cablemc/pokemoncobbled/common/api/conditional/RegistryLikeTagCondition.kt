package com.cablemc.pokemoncobbled.common.api.conditional

import com.google.gson.JsonElement
import net.minecraft.tag.TagKey
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey

/**
 * A condition for some registry type that asserts that the entry must be inside the given [TagKey]. This is presented
 * in JSONs as an identifier prefixed with a hash.
 *
 * @author Hiroku
 * @since July 16th, 2022
 */
open class RegistryLikeTagCondition<T>(val tag: TagKey<T>) : RegistryLikeCondition<T> {

    companion object {
        const val PREFIX = "#"
        fun <T> resolver(
            registryKey: RegistryKey<Registry<T>>,
            constructor: (TagKey<T>) -> RegistryLikeTagCondition<T>
        ): (JsonElement) -> RegistryLikeTagCondition<T>? = {
            val firstSymbol = it.asString.substring(0, 1)
            if (firstSymbol == PREFIX) {
                val identifier = Identifier(it.asString.substring(1))
                constructor(TagKey.of(registryKey, identifier))
            } else {
                null
            }
        }
    }

    override fun fits(t: T, registry: Registry<T>): Boolean {
        val registryHasTag = registry.containsTag(tag)
        if (!registryHasTag) {
            return false
        }

        return registry.getKey(t)
            .flatMap(registry::getEntry)
            .map { entry -> entry.isIn(tag) }
            .orElse(false)
    }
}