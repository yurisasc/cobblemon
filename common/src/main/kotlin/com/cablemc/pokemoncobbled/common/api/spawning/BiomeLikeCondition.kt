package com.cablemc.pokemoncobbled.common.api.spawning

import com.cablemc.pokemoncobbled.common.util.adapters.BiomeConditionCollectionAdapter
import com.google.gson.reflect.TypeToken
import net.minecraft.util.registry.DynamicRegistryManager
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
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
         * @param id The unique ID of this variant. You may also match existing ones in order to replace them.
         * @param variantType The type of the [BiomeLikeCondition].
         * @param valueType The type of the [BiomeLikeCondition.requiredValue].
         */
        fun <T : Any> registerVariant(id: String, variantType: KClass<out BiomeLikeCondition<T>>, valueType: TypeToken<T>, factory: (T) -> BiomeLikeCondition<T>) {
            BiomeConditionCollectionAdapter.registerVariant(id, variantType, valueType)
        }

    }

}