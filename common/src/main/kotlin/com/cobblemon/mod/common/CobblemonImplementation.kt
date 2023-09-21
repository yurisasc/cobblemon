/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common

import com.cobblemon.mod.common.api.net.ClientNetworkPacketHandler
import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.api.net.ServerNetworkPacketHandler
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.serialization.Codec
import kotlin.reflect.KClass
import net.minecraft.advancement.criterion.Criterion
import net.minecraft.command.argument.serialize.ArgumentSerializer
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.tag.TagKey
import net.minecraft.resource.ResourceReloader
import net.minecraft.resource.ResourceType
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.world.GameRules
import net.minecraft.world.biome.Biome
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.feature.PlacedFeature
import org.jetbrains.annotations.ApiStatus

interface CobblemonImplementation {
    val modAPI: ModAPI

    /**
     *
     */
    val networkManager: NetworkManager

    /**
     * TODO
     *
     * @return
     */
    fun environment(): Environment

    /**
     * TODO
     *
     * @param id
     * @return
     */
    fun isModInstalled(id: String): Boolean

    /**
     * TODO
     *
     */
    fun registerPermissionValidator()

    /**
     * TODO
     *
     */
    fun registerSoundEvents()

    /**
     * TODO
     *
     */
    fun registerItems()

    /**
     * TODO
     *
     */
    fun registerBlocks()

    /**
     * TODO
     *
     */
    fun registerEntityTypes()

    /**
     * TODO
     *
     */
    fun registerEntityAttributes()

    /**
     * TODO
     *
     */
    fun registerBlockEntityTypes()

    /**
     * TODO
     *
     */
    fun registerWorldGenFeatures()

    fun registerParticles()

    /**
     * Add a feature to the current platform implementation.
     *
     * @param feature The [PlacedFeature] being added.
     * @param step The [GenerationStep.Feature] of this feature.
     * @param validTag The [TagKey] required by the [Biome] for this feature to generate in, if null all biomes are valid.
     */
    fun addFeatureToWorldGen(feature: RegistryKey<PlacedFeature>, step: GenerationStep.Feature, validTag: TagKey<Biome>?)

    /**
     * TODO
     *
     * @param A
     * @param T
     * @param identifier
     * @param argumentClass
     * @param serializer
     */
    fun <A : ArgumentType<*>, T : ArgumentSerializer.ArgumentTypeProperties<A>> registerCommandArgument(identifier: Identifier, argumentClass: KClass<A>, serializer: ArgumentSerializer<A, T>)

    /**
     * TODO
     *
     * @param T
     * @param name
     * @param category
     * @param type
     * @return
     */
    fun <T : GameRules.Rule<T>> registerGameRule(name: String, category: GameRules.Category, type: GameRules.Type<T>): GameRules.Key<T>

    /**
     * TODO
     *
     * @param T
     * @param criteria
     * @return
     */
    fun <T : Criterion<*>> registerCriteria(criteria: T): T

    /**
     * TODO
     *
     * @param identifier
     * @param reloader
     * @param type
     * @param dependencies
     */
    fun registerResourceReloader(identifier: Identifier, reloader: ResourceReloader, type: ResourceType, dependencies: Collection<Identifier>)

    /**
     * TODO
     *
     * @return
     */
    fun server(): MinecraftServer?

    /**
     * Queue the creation of a registry in the implementation.
     * This registry will not be immediately available for access.
     *
     * @param T The type of the registry entries.
     * @param registryKey The [RegistryKey] of this registry.
     * @param codec The [Codec] used for this registry entries to be parsed on the server.
     * @param networkCodec The [Codec] used when the client is synchronizing this registry from the server. If this is null the client will not require the registry aka not load any data.
     *
     * @see getRegistry
     */
    @ApiStatus.Internal
    fun <T> createRegistry(registryKey: RegistryKey<Registry<T>>, codec: Codec<T>, networkCodec: Codec<T>? = null)

    /**
     * Fetches a registry.
     * This is intended for Cobblemon registries but can be used for any.
     *
     * @param T The type of the registry.
     * @param registryKey The [RegistryKey] of the registry being fetched.
     * @return The registry if present.
     *
     * @throws Exception If the registry doesn't exist or isn't loaded yet.
     * @see createRegistry
     */
    @ApiStatus.Internal
    fun <T> getRegistry(registryKey: RegistryKey<Registry<T>>): Registry<T>

}

enum class ModAPI {
    FABRIC,
    FORGE
}

interface NetworkManager {

    fun registerClientBound()

    fun registerServerBound()

    fun <T: NetworkPacket<T>> createClientBound(identifier: Identifier, kClass: KClass<T>, encoder: (T, PacketByteBuf) -> Unit, decoder: (PacketByteBuf) -> T, handler: ClientNetworkPacketHandler<T>)

    fun <T: NetworkPacket<T>> createServerBound(identifier: Identifier, kClass: KClass<T>, encoder: (T, PacketByteBuf) -> Unit, decoder: (PacketByteBuf) -> T, handler: ServerNetworkPacketHandler<T>)

    fun sendPacketToPlayer(player: ServerPlayerEntity, packet: NetworkPacket<*>)

    fun sendPacketToServer(packet: NetworkPacket<*>)

    fun <T : NetworkPacket<*>> asVanillaClientBound(packet: T): Packet<ClientPlayPacketListener>

}

enum class Environment {

    CLIENT,
    SERVER

}
