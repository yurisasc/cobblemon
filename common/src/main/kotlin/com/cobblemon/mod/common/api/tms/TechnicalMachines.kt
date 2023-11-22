package com.cobblemon.mod.common.api.tms

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

object TechnicalMachines : JsonDataRegistry<TechnicalMachine> {
    override val gson = GsonBuilder()
        .registerTypeAdapter(Identifier::class.java, IdentifierAdapter)
        .create()
    override val typeToken = TypeToken.get(TechnicalMachine::class.java)
    override val resourcePath = "tms"
    override val id = cobblemonResource("technical_machines")
    override val type = ResourceType.SERVER_DATA
    override val observable = SimpleObservable<TechnicalMachines>()

    val tmMap = mutableMapOf<Identifier, TechnicalMachine>()
    override fun reload(data: Map<Identifier, TechnicalMachine>) {
        data.forEach {id, tm ->
            tmMap[id] = tm
        }
    }
    override fun sync(player: ServerPlayerEntity) { }


}