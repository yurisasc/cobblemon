/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.types

import com.cobblemon.mod.common.api.data.ShowdownIdentifiable
import com.cobblemon.mod.common.api.registry.RegistryElement
import com.cobblemon.mod.common.api.resistance.Resistance
import com.cobblemon.mod.common.api.resistance.Resistible
import com.cobblemon.mod.common.api.resistance.registry.ResistibleType
import com.cobblemon.mod.common.api.resistance.registry.ResistibleTypes
import com.cobblemon.mod.common.registry.CobblemonRegistries
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
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
    val damageTaken: Map<out Resistible, Resistance>
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
        return ShowdownIdentifiable.REGEX.replace(this.resourceLocation().toString().lowercase(), "")
    }

    override fun resistanceTo(other: Resistible): Resistance {
        return this.damageTaken[other] ?: Resistance.NEUTRAL
    }

    override fun resistibleType(): ResistibleType<*> = ResistibleTypes.ELEMENTAL_TYPE

    companion object {
        @JvmStatic
        val CODEC: Codec<ElementalType> = RecordCodecBuilder.create { instance ->
            instance.group(
                ComponentSerialization.CODEC.fieldOf("displayName").forGetter(ElementalType::displayName),
                ColorRGBA.CODEC.fieldOf("color").forGetter(ElementalType::color),
                Codec.INT.fieldOf("textureXMultiplier").forGetter(ElementalType::textureXMultiplier),
                ResourceLocation.CODEC.fieldOf("texture").forGetter(ElementalType::texture),
                Codec.unboundedMap(ResistibleType.codec(), Resistance.CODEC)
                    .fieldOf("damageTaken")
                    .forGetter { it.damageTaken.toMutableMap() }
            ).apply(instance, ::ElementalType)
        }
    }

}