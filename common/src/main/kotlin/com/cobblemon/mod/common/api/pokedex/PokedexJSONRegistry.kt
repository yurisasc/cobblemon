package com.cobblemon.mod.common.api.pokedex

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.data.JsonDataRegistry
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies.getByIdentifier
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.net.messages.client.data.PokedexSyncPacket
import com.cobblemon.mod.common.net.messages.client.data.SpeciesRegistrySyncPacket
import com.cobblemon.mod.common.pokedex.DexData
import com.cobblemon.mod.common.pokedex.DexPokemonData
import com.cobblemon.mod.common.pokemon.Species
import com.cobblemon.mod.common.util.cobblemonResource
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import net.minecraft.resource.ResourceType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

object PokedexJSONRegistry : JsonDataRegistry<DexData> {
    override val id = cobblemonResource("dex")
    override val type = ResourceType.SERVER_DATA

    override val gson: Gson = GsonBuilder()
        .disableHtmlEscaping()
        .enableComplexMapKeySerialization()
        .create()

    override val typeToken: TypeToken<DexData> = TypeToken.get(DexData::class.java)
    override val resourcePath = "dex"

    override val observable = SimpleObservable<PokedexJSONRegistry>()

    private val dexByIdentifier = hashMapOf<Identifier, DexData>()
    val dexes: MutableCollection<DexData>
        get() = this.dexByIdentifier.values

    /**
     * Finds a dex by the pathname of their [Identifier].
     * This method exists for the convenience of finding Cobble default dexes.
     * This uses [getByIdentifier] using the [Cobblemon.MODID] as the namespace and the [name] as the path.
     *
     * @param name The path of the species asset.
     * @return The [DexData] if existing.
     */
    fun getByName(name: String) = this.getByIdentifier(cobblemonResource(name))

    /**
     * Finds a [DexData] by its unique [Identifier].
     *
     * @param identifier The unique [DexData.resourceIdentifier] of the [DexData].
     * @return The [DexData] if existing.
     */
    fun getByIdentifier(identifier: Identifier) = this.dexByIdentifier[identifier]

    /**
     * Counts the currently loaded dexes.
     *
     * @return The loaded dex amount.
     */
    fun count() = this.dexByIdentifier.size

    /**
     * Gets a list of dexes in namespace.
     *
     * @return The dex list in namespace.
     */
    fun getDexInNamespace(namespace: String = Cobblemon.MODID): Collection<DexData> = dexes.filter { it.identifier.namespace == namespace }.toList()

    /**
     * Get a collection of loaded namespaces.
     *
     * @return The collection of loaded namespaces.
     */
    fun getNamespaces() : Collection<String> = dexes.map {it.identifier.namespace}.toSet()

    override fun reload(data: Map<Identifier, DexData>) {
        this.dexes.clear()
        data.forEach { (identifier, dexData) ->
            dexData.identifier = identifier
            this.dexByIdentifier[identifier] = dexData
        }
        dexes.addAll(dexByIdentifier.values)
    }

    override fun sync(player: ServerPlayerEntity) {
        PokedexSyncPacket(dexes.toList()).sendToPlayer(player)
    }
}