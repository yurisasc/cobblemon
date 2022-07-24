package com.cablemc.pokemoncobbled.common.api.conditional

import net.minecraft.util.registry.Registry

/**
 * A condition which applies to an entry in some [Registry].
 *
 * @author Hiroku
 * @since July 16th, 2022
 */
interface RegistryLikeCondition<T> {
    fun fits(t: T, registry: Registry<T>): Boolean
}