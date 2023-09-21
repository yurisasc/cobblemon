/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.types

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.data.ShowdownIdentifiable
import com.cobblemon.mod.common.api.registry.CobblemonRegistries
import com.cobblemon.mod.common.api.registry.CobblemonRegistryElement
import com.cobblemon.mod.common.api.types.client.ClientData
import com.cobblemon.mod.common.api.types.hiddenpower.HiddenPowerRequirement
import com.cobblemon.mod.common.api.types.resistance.*
import com.cobblemon.mod.common.util.codec.setCodec
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.registry.Registry
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.util.Optional

/**
 * Class representing the elemental type of Pokémon or Move.
 *
 * @param damageTaken The [RawResistanceInformation] that will be converted into [ResistanceInformation] for this type.
 * @param hiddenPowerRequirement The [HiddenPowerRequirement] for this elemental type. If not present that means the type doesn't support hidden power.
 * @param clientData The [ClientData] of this type.
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class ElementalType(
    private val damageTaken: Set<RawResistanceInformation>,
    val hiddenPowerRequirement: Optional<HiddenPowerRequirement>,
    val clientData: ClientData
): CobblemonRegistryElement<ElementalType>, ShowdownIdentifiable {

    /**
     * The translatable component responsible for showing this
     */
    val displayName: MutableText by lazy { Text.translatable("${this.id().namespace}.type.${this.id().path.replace("/", ".")}") }

    /**
     * The resolved [ResistanceInformation]s.
     * This is done by lazily invoking [RawResistanceInformation.toImplementation].
     */
    private val resistanceInformation: HashSet<ResistanceInformation> by lazy {
        val resulting = this.damageTaken.map(RawResistanceInformation::toImplementation).toHashSet()
        this.populateMissingTypes(resulting)
        return@lazy resulting
    }

    /**
     * TODO
     *
     * @return
     */
    fun resistanceInformation(): Set<ResistanceInformation> = this.resistanceInformation

    /**
     * TODO
     *
     * @param other
     * @return
     */
    fun resistanceAgainst(other: ElementalType): Resistance {
        return this.resistanceInformation().firstOrNull { it is ElementalTypeResistanceInformation && it.elementalType == other }?.resistance
            ?: throw IllegalArgumentException("Cannot find type resistance for ${other.id()} this should be impossible.")
    }

    override fun registryEntry(): RegistryEntry<ElementalType> = this.registry().getEntry(this)

    override fun id(): Identifier = this.registry().getId(this)!!

    override fun registry(): Registry<ElementalType> = CobblemonRegistries.ELEMENTAL_TYPE

    override fun showdownId(): String {
        val showdownId = ShowdownIdentifiable.REGEX.replace(this.id().path.lowercase(), "")
        if (this.id().namespace == Cobblemon.MODID) {
            return showdownId
        }
        return this.id().namespace + showdownId
    }

    override fun equals(other: Any?): Boolean = other is ElementalType && other.id() == this.id()

    override fun hashCode(): Int = this.id().hashCode()

    /**
     * Adds in any missing [ResistanceInformation] for other loaded [ElementalType]s.
     *
     * @param current The currently loaded [ResistanceInformation].
     */
    private fun populateMissingTypes(current: HashSet<ResistanceInformation>) {
        CobblemonRegistries.ELEMENTAL_TYPE.forEach { type ->
            if (current.none { it is ElementalTypeResistanceInformation && it.elementalType == type }) {
                val information = ElementalTypeResistanceInformation(type, Resistance.NEUTRAL)
                current.add(information)
                Cobblemon.LOGGER.info("{} elemental type was missing resistance information against {}, defaulting to neutral, it is recommended to validate if this is intended or if you should add additional data for the missing type", this.id(), type.id())
            }
        }
    }

    companion object {

        val CODEC: Codec<ElementalType> = RecordCodecBuilder.create { builder ->
            builder.group(
                setCodec(RawResistanceInformation.CODEC).fieldOf("damageTaken").forGetter(ElementalType::damageTaken),
                HiddenPowerRequirement.CODEC.optionalFieldOf("hiddenPowerRequirement").forGetter(ElementalType::hiddenPowerRequirement),
                ClientData.CODEC.fieldOf("clientData").forGetter(ElementalType::clientData)
            ).apply(builder, ::ElementalType)
        }

        val NETWORK_CODEC: Codec<ElementalType> = RecordCodecBuilder.create { builder ->
            builder.group(
                setCodec(RawResistanceInformation.CODEC).fieldOf("damageTaken").forGetter(ElementalType::damageTaken),
                ClientData.CODEC.fieldOf("clientData").forGetter(ElementalType::clientData)
            ).apply(builder) { damageTaken, clientData -> ElementalType(damageTaken, Optional.empty(), clientData) }
        }

    }

}