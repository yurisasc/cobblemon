/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.data.elemental

import com.cobblemon.mod.common.api.effect.Effect
import com.cobblemon.mod.common.api.effect.types.ElementalTypeEffect
import com.cobblemon.mod.common.api.effect.types.ShowdownConditionEffect
import com.cobblemon.mod.common.api.resistance.Resistance
import com.cobblemon.mod.common.api.resistance.ResistanceMap
import com.cobblemon.mod.common.api.types.ElementalType
import com.cobblemon.mod.common.api.types.ElementalTypes
import com.cobblemon.mod.common.data.DataExport
import com.cobblemon.mod.common.data.OutputtingDataProvider
import com.cobblemon.mod.common.registry.CobblemonRegistries
import com.cobblemon.mod.common.util.cobblemonResource
import com.mojang.serialization.Codec
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.ColorRGBA
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

class ElementalTypeProvider(
    packOutput: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>
) : OutputtingDataProvider<ElementalType, ElementalTypeProvider.ElementalTypeDataExport>(packOutput, lookupProvider) {

    override fun buildEntries(lookupProvider: HolderLookup.Provider, consumer: Consumer<ElementalTypeDataExport>) {
        consumer.accept(dataExport("bug", 0xA2C831, 11, bugResistances()))
        consumer.accept(dataExport("dark", 0x5C6CB2, 15, darkResistances()))
        consumer.accept(dataExport("dragon", 0x535DE8, 14, dragonResistances()))
        consumer.accept(dataExport("electric", 0xEFD128, 4, electricResistances()))
        consumer.accept(dataExport("fairy", 0xEA727E, 17, fairyResistances()))
        consumer.accept(dataExport("fighting", 0xC44C5C, 6, fightingResistances()))
        consumer.accept(dataExport("fire", 0xE55C32, 1, fireResistances()))
        consumer.accept(dataExport("flying", 0xBCC1FF, 9, flyingResistances()))
        consumer.accept(dataExport("ghost", 0x9572E5, 13, ghostResistances()))
        consumer.accept(dataExport("grass", 0x4DBC3C, 3, grassResistances()))
        consumer.accept(dataExport("ground", 0xD89950, 8, groundResistances()))
        consumer.accept(dataExport("ice", 0x6BC3EF, 5, iceResistances()))
        consumer.accept(dataExport("normal", 0xDDDDCF, 0, normalResistances()))
        consumer.accept(dataExport("poison", 0xA24BD8, 7, poisonResistances()))
        consumer.accept(dataExport("psychic", 0xD86AD6, 10, psychicResistances()))
        consumer.accept(dataExport("rock", 0xAA9666, 12, rockResistances()))
        consumer.accept(dataExport("steel", 0xC3CCE0, 16, steelResistances()))
        consumer.accept(dataExport("water", 0x4A9BE8, 2, waterResistances()))
    }

    override fun getName(): String = "elemental types"

    override fun pathProvider(): PackOutput.PathProvider = this.createPathForCobblemonData(CobblemonRegistries.ELEMENTAL_TYPE_KEY)

    private fun dataExport(
        name: String,
        color: Int,
        textureOffset: Int,
        resistances: Map<Effect, Resistance>
    ): ElementalTypeDataExport {
        val type = ElementalType(
            Component.translatable("cobblemon.type.$name"),
            ColorRGBA(color),
            textureOffset,
            cobblemonResource("ui/types.png"),
            ResistanceMap(resistances)
        )
        return ElementalTypeDataExport(cobblemonResource(name), type)
    }

    private fun bugResistances(): Map<Effect, Resistance> = mapOf(
        elementalType(ElementalTypes.FIGHTING) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.FIRE) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.FLYING) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.GRASS) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.GROUND) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.ROCK) to Resistance.SUPER_EFFECTIVE,
    )

    // TODO: Prankster immune pending on ability registry
    private fun darkResistances(): Map<Effect, Resistance> = mapOf(
        elementalType(ElementalTypes.BUG) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.DARK) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.FAIRY) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.FIGHTING) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.GHOST) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.PSYCHIC) to Resistance.IMMUNE,
    )

    private fun dragonResistances(): Map<Effect, Resistance> = mapOf(
        elementalType(ElementalTypes.DRAGON) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.ELECTRIC) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.FAIRY) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.FIRE) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.GRASS) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.ICE) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.WATER) to Resistance.NOT_VERY_EFFECTIVE,
    )

    // TODO: Paralysis immune pending on status registry
    private fun electricResistances(): Map<Effect, Resistance> = mapOf(
        elementalType(ElementalTypes.ELECTRIC) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.FLYING) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.GROUND) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.STEEL) to Resistance.NOT_VERY_EFFECTIVE,
    )

    private fun fairyResistances(): Map<Effect, Resistance> = mapOf(
        elementalType(ElementalTypes.BUG) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.DARK) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.DRAGON) to Resistance.IMMUNE,
        elementalType(ElementalTypes.FIGHTING) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.POISON) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.STEEL) to Resistance.SUPER_EFFECTIVE,
    )

    private fun fightingResistances(): Map<Effect, Resistance> = mapOf(
        elementalType(ElementalTypes.BUG) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.DARK) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.FAIRY) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.FLYING) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.PSYCHIC) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.ROCK) to Resistance.NOT_VERY_EFFECTIVE,
    )

    // TODO: Burn immune pending on status registry
    private fun fireResistances(): Map<Effect, Resistance> = mapOf(
        elementalType(ElementalTypes.BUG) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.FAIRY) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.FIRE) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.GRASS) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.GROUND) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.ICE) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.ROCK) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.STEEL) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.WATER) to Resistance.SUPER_EFFECTIVE,
    )

    private fun flyingResistances(): Map<Effect, Resistance> = mapOf(
        elementalType(ElementalTypes.BUG) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.ELECTRIC) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.FIGHTING) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.GRASS) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.GROUND) to Resistance.IMMUNE,
        elementalType(ElementalTypes.ICE) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.ROCK) to Resistance.SUPER_EFFECTIVE,
    )

    private fun ghostResistances(): Map<Effect, Resistance> = mapOf(
        elementalType(ElementalTypes.BUG) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.DARK) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.FIGHTING) to Resistance.IMMUNE,
        elementalType(ElementalTypes.GHOST) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.NORMAL) to Resistance.IMMUNE,
        elementalType(ElementalTypes.POISON) to Resistance.NOT_VERY_EFFECTIVE,
        showdownCondition("trapped") to Resistance.IMMUNE,
    )

    private fun grassResistances(): Map<Effect, Resistance> = mapOf(
        elementalType(ElementalTypes.BUG) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.ELECTRIC) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.FIRE) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.FLYING) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.GRASS) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.GROUND) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.ICE) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.POISON) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.WATER) to Resistance.NOT_VERY_EFFECTIVE,
        showdownCondition("powder") to Resistance.IMMUNE,
    )

    private fun groundResistances(): Map<Effect, Resistance> = mapOf(
        elementalType(ElementalTypes.ELECTRIC) to Resistance.IMMUNE,
        elementalType(ElementalTypes.GRASS) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.ICE) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.POISON) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.ROCK) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.WATER) to Resistance.SUPER_EFFECTIVE,
        showdownCondition("sandstorm") to Resistance.IMMUNE,
    )

    // TODO: frozen immune
    private fun iceResistances(): Map<Effect, Resistance> = mapOf(
        elementalType(ElementalTypes.FIGHTING) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.FIRE) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.ICE) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.ROCK) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.STEEL) to Resistance.SUPER_EFFECTIVE,
        showdownCondition("hail") to Resistance.IMMUNE,
    )

    private fun normalResistances(): Map<Effect, Resistance> = mapOf(
        elementalType(ElementalTypes.FIGHTING) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.GHOST) to Resistance.IMMUNE,
    )

    // TODO: poison, toxic immune
    private fun poisonResistances(): Map<Effect, Resistance> = mapOf(
        elementalType(ElementalTypes.BUG) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.FAIRY) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.FIGHTING) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.GRASS) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.GROUND) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.POISON) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.PSYCHIC) to Resistance.SUPER_EFFECTIVE,
    )

    private fun psychicResistances(): Map<Effect, Resistance> = mapOf(
        elementalType(ElementalTypes.BUG) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.DARK) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.FIGHTING) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.GHOST) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.PSYCHIC) to Resistance.NOT_VERY_EFFECTIVE,
    )

    private fun rockResistances(): Map<Effect, Resistance> = mapOf(
        elementalType(ElementalTypes.FIGHTING) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.FIRE) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.FLYING) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.GRASS) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.GROUND) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.NORMAL) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.POISON) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.STEEL) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.WATER) to Resistance.SUPER_EFFECTIVE,
        showdownCondition("sandstorm") to Resistance.IMMUNE,
    )

    // TODO: poison, toxic
    private fun steelResistances(): Map<Effect, Resistance> = mapOf(
        elementalType(ElementalTypes.BUG) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.DRAGON) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.FAIRY) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.FIGHTING) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.FIRE) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.FLYING) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.GRASS) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.GROUND) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.ICE) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.NORMAL) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.POISON) to Resistance.IMMUNE,
        elementalType(ElementalTypes.PSYCHIC) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.ROCK) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.STEEL) to Resistance.NOT_VERY_EFFECTIVE,
        showdownCondition("sandstorm") to Resistance.IMMUNE,
    )

    private fun waterResistances(): Map<Effect, Resistance> = mapOf(
        elementalType(ElementalTypes.ELECTRIC) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.FIRE) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.GRASS) to Resistance.SUPER_EFFECTIVE,
        elementalType(ElementalTypes.ICE) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.STEEL) to Resistance.NOT_VERY_EFFECTIVE,
        elementalType(ElementalTypes.WATER) to Resistance.NOT_VERY_EFFECTIVE,
    )
    
    private fun elementalType(key: ResourceKey<ElementalType>): Effect {
        return ElementalTypeEffect(key)
    }

    private fun showdownCondition(key: String): Effect {
        return ShowdownConditionEffect(key)
    }

    class ElementalTypeDataExport(
        private val id: ResourceLocation,
        private val value: ElementalType
    ) : DataExport<ElementalType> {
        override fun id(): ResourceLocation = this.id

        override fun codec(): Codec<ElementalType> = ElementalType.CODEC

        override fun value(): ElementalType = this.value
    }

}