package com.cablemc.pokemoncobbled.common.api.registry

import net.minecraft.resources.ResourceLocation

/**
 * Used for attributes that will be a part of a [Registry].
 *
 * @author Licious
 * @since March 2nd, 2022
 */
interface Keyed {

    val id: ResourceLocation

}
