package com.cobblemon.mod.common.client.render.models.blockbench.repository

import com.cobblemon.mod.common.api.data.DataRegistry
import com.cobblemon.mod.common.api.data.JsonDataRegistry
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.client.render.models.blockbench.TexturedModel
import com.cobblemon.mod.common.util.cobblemonResource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.minecraft.resource.ResourceType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

/**
 *  A repo to load our ONE egg model lmao
 */
object EggModelRepo : JsonDataRegistry<TexturedModel> {
    override val gson = TexturedModel.GSON
    override val typeToken = TypeToken.get(TexturedModel::class.java)
    override val resourcePath = "bedrock/egg_models"
    override val id = cobblemonResource("egg_models")
    override val type = ResourceType.CLIENT_RESOURCES
    override val observable = SimpleObservable<EggModelRepo>()
    val eggModels = hashMapOf<Identifier, TexturedModel>()

    override fun reload(data: Map<Identifier, TexturedModel>) {
        eggModels.putAll(data)
    }

    override fun sync(player: ServerPlayerEntity) {
    }

}