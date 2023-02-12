/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common

import com.cobblemon.mod.common.api.net.NetworkPacket
import com.mojang.brigadier.arguments.ArgumentType
import net.minecraft.command.argument.serialize.ArgumentSerializer
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.tag.TagKey
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.world.biome.Biome
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.feature.PlacedFeature
import kotlin.reflect.KClass

interface CobblemonImplementation {

    fun isModInstalled(id: String): Boolean

    fun registerPermissionValidator()

    fun registerSoundEvents()

    fun registerItems()

    fun registerBlocks()

    fun registerEntityTypes()

    fun registerEntityAttributes()

    fun registerBlockEntityTypes()

    fun registerWorldGenFeatures()

    /**
     * Add a feature to the current platform implementation.
     *
     * @param feature The [PlacedFeature] being added.
     * @param step The [GenerationStep.Feature] of this feature.
     * @param validTag The [TagKey] required by the [Biome] for this feature to generate in, if null all biomes are valid.
     */
    fun addFeatureToWorldGen(feature: RegistryKey<PlacedFeature>, step: GenerationStep.Feature, validTag: TagKey<Biome>?)

    fun <A : ArgumentType<*>, T : ArgumentSerializer.ArgumentTypeProperties<A>> registerCommandArgument(identifier: Identifier, argumentClass: KClass<A>, serializer: ArgumentSerializer<A, T>)

}

interface NetworkDelegate {

    fun sendPacketToPlayer(player: ServerPlayerEntity, packet: NetworkPacket)
    fun sendPacketToServer(packet: NetworkPacket)
    fun <T : NetworkPacket> buildMessage(
        packetClass: Class<T>,
        toServer: Boolean
    ): CobblemonNetwork.PreparedMessage<T>
}