/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.types

import com.cobblemon.mod.common.api.data.ShowdownIdentifiable
import com.cobblemon.mod.common.api.effect.Effect
import com.cobblemon.mod.common.api.effect.types.ElementalTypeEffect
import com.cobblemon.mod.common.api.registry.RegistryElement
import com.cobblemon.mod.common.api.resistance.Resistance
import com.cobblemon.mod.common.api.resistance.ResistanceMap
import com.cobblemon.mod.common.api.resistance.Resistible
import com.cobblemon.mod.common.pokemon.types.ShowdownElementalTypeDTO
import com.cobblemon.mod.common.registry.CobblemonRegistries
import com.cobblemon.mod.common.util.simplify
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.util.ColorRGBA

class ElementalType(
    val displayName: Component,
    val color: ColorRGBA,
    val textureXMultiplier: Int,
    val texture: ResourceLocation,
    val damageTaken: ResistanceMap
) : RegistryElement<ElementalType>, ShowdownIdentifiable, Resistible {

    // TODO: Remove me later
    val name: String get() = this.resourceLocation().path

    override fun registry(): Registry<ElementalType> = CobblemonRegistries.ELEMENTAL_TYPE

    override fun resourceKey(): ResourceKey<ElementalType> = this.registry().getResourceKey(this)
        .orElseThrow { IllegalStateException("Unregistered ElementalType") }

    override fun isTaggedBy(tag: TagKey<ElementalType>): Boolean = this.registry()
        .getHolder(this.resourceKey())
        .orElseThrow { IllegalStateException("Unregistered ElementalType") }
        .`is`(tag)

    override fun showdownId(): String {
        return ShowdownIdentifiable.REGEX.replace(this.resourceLocation().simplify().lowercase(), "")
    }

    override fun resistanceTo(effect: Effect): Resistance {
        return this.damageTaken[effect] ?: Resistance.NEUTRAL
    }

    override fun asEffect(): Effect = ElementalTypeEffect(this.resourceKey())

    internal fun asShowdownDTO(): ShowdownElementalTypeDTO = ShowdownElementalTypeDTO(
        this.showdownId(),
        this.damageTaken.map { it.key.showdownId() to it.value.showdownValue }.toMap()
    )

    companion object {
        @JvmStatic
        val CODEC: Codec<ElementalType> = RecordCodecBuilder.create { instance ->
            instance.group(
                ComponentSerialization.CODEC.fieldOf("displayName").forGetter(ElementalType::displayName),
                ColorRGBA.CODEC.fieldOf("color").forGetter(ElementalType::color),
                Codec.INT.fieldOf("textureXMultiplier").forGetter(ElementalType::textureXMultiplier),
                ResourceLocation.CODEC.fieldOf("texture").forGetter(ElementalType::texture),
                ResistanceMap.CODEC.fieldOf("damageTaken").forGetter(ElementalType::damageTaken)
            ).apply(instance, ::ElementalType)
        }
    }

}