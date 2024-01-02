package com.cobblemon.mod.common.api.pokemon.breeding

import com.cobblemon.mod.common.api.data.DataRegistry
import com.cobblemon.mod.common.api.data.JsonDataRegistry
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.util.adapters.IdentifierAdapter
import com.cobblemon.mod.common.util.cobblemonResource
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import net.minecraft.resource.ResourceType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

object EggPatterns : JsonDataRegistry<EggPattern> {
    override val id = cobblemonResource("egg_patterns")
    override val type = ResourceType.SERVER_DATA
    override val observable = SimpleObservable<EggPatterns>()
    override val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Identifier::class.java, IdentifierAdapter)
        .create()
    override val typeToken = TypeToken.get(EggPattern::class.java)
    override val resourcePath = "egg_patterns"

    val patternMap = hashMapOf<Identifier, EggPattern>()
    override fun reload(data: Map<Identifier, EggPattern>) {
        patternMap.putAll(data)
    }

    override fun sync(player: ServerPlayerEntity) {
        //Need to implement
    }
}