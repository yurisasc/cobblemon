package com.cablemc.pokemoncobbled.common.api.spawning

import com.cablemc.pokemoncobbled.common.util.adapters.BiomeLikeConditionAdapter
import com.google.gson.reflect.TypeToken
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.world.biome.Biome
import kotlin.reflect.KClass

/**
 * A condition that holds a value used to match against a given biome.
 * Any implementation must have a primary constructor whose sole param will be the [BiomeLikeCondition.requiredValue].
 *
 * @param T The type of the required value.
 *
 * @author Licious
 * @since July 1st, 2022
 */
interface BiomeLikeCondition<T> {

    /**
     * The expected value of type [T] for [accepts] to return true.
     */
    val requiredValue: T

    /**
     * Checks if the given [Biome] is valid for the [requiredValue].
     *
     * @param biome The [Biome] the spawn is happening in.
     * @param registry The [Biome] [Registry] of the world the spawn is happening in.
     * @return If the [requiredValue] is satisfied.
     */
    fun accepts(biome: Biome, registry: Registry<Biome>): Boolean

    companion object {

        /**
         * Registers a [BiomeLikeCondition] variant to the adapter.
         *
         * @param prefix The prefix of this variant. You may also match existing ones in order to replace them. This prefix must not return true for [Identifier.isCharValid].
         * @param variantType The type of the [BiomeLikeCondition].
         * @param valueType The type of the [BiomeLikeCondition.requiredValue].
         *
         * @throws IllegalArgumentException if the [prefix] returns true for [Identifier.isCharValid]
         */
        fun <T : Any> registerVariant(prefix: Char, variantType: KClass<out BiomeLikeCondition<T>>, valueType: TypeToken<T>) {
            if (Identifier.isCharValid(prefix)) {
                throw IllegalArgumentException("Cannot register a prefix reserved for Identifier namespaces: [a-z0-9_.-]")
            }
            BiomeLikeConditionAdapter.registerVariant(prefix, variantType, valueType)
        }

    }

}