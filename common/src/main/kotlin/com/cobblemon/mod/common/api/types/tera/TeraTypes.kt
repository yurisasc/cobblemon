/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.types.tera

import com.cobblemon.mod.common.api.registry.CobblemonRegistry
import com.cobblemon.mod.common.api.types.ElementalType
import com.cobblemon.mod.common.registry.CobblemonRegistries
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation

/**
 * The registry of all [TeraType]s.
 */
@Suppress("unused")
object TeraTypes : CobblemonRegistry<TeraType>() {

    @JvmStatic val NORMAL = this.key("normal")
    @JvmStatic val FIRE = this.key("fire")
    @JvmStatic val WATER = this.key("water")
    @JvmStatic val GRASS = this.key("grass")
    @JvmStatic val ELECTRIC = this.key("electric")
    @JvmStatic val ICE = this.key("ice")
    @JvmStatic val FIGHTING = this.key("fighting")
    @JvmStatic val POISON = this.key("poison")
    @JvmStatic val GROUND = this.key("ground")
    @JvmStatic val FLYING = this.key("flying")
    @JvmStatic val PSYCHIC = this.key("psychic")
    @JvmStatic val BUG = this.key("bug")
    @JvmStatic val ROCK = this.key("rock")
    @JvmStatic val GHOST = this.key("ghost")
    @JvmStatic val DRAGON = this.key("dragon")
    @JvmStatic val DARK = this.key("dark")
    @JvmStatic val STEEL = this.key("steel")
    @JvmStatic val FAIRY = this.key("fairy")
    @JvmStatic val STELLAR = this.key("stellar")

    /**
     * Gets a [TeraType] by its [id].
     *
     * @param id The [ResourceLocation] expected to match against a [TeraType.id].
     * @return The found [TeraType] or null.
     */
    @JvmStatic
    fun get(id: ResourceLocation): TeraType? = CobblemonRegistries.TERA_TYPE
        .get(id)

    /**
     * Gets a [TeraType] by its [id].
     *
     * @param id The string representation of a [ResourceLocation] if no namespace is present assumes Cobblemon' instead of Minecraft'.
     * @return The found [TeraType] or null.
     */
    @JvmStatic
    fun get(id: String): TeraType? = this.get(cobblemonResource(id))

    /**
     * Gets the corresponding tera type for a [ElementalType].
     *
     * @param type The [ElementalType] being checked.
     * @return The associated [TeraType].
     */
    @JvmStatic
    fun forElementalType(type: ElementalType): TeraType = this.get(type.resourceLocation())!! // it's safe to do

    override fun registry(): Registry<TeraType> = CobblemonRegistries.TERA_TYPE

    override fun registryKey(): ResourceKey<Registry<TeraType>> = CobblemonRegistries.TERA_TYPE_KEY
}