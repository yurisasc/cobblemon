/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.repository

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.data.JsonDataRegistry
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.client.render.CobblemonAtlases
import com.cobblemon.mod.common.client.render.models.blockbench.TexturedModel
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.endsWith
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jozufozu.flywheel.core.hardcoded.ModelPart
import com.jozufozu.flywheel.core.hardcoded.PartBuilder
import net.minecraft.client.MinecraftClient
import net.minecraft.resource.ResourceType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

/**
 * The data registry responsible for berry fruit and flower models.
 * This is only present on the client.
 */
object BerryModelRepository : JsonDataRegistry<TexturedModel> {

    override val id = cobblemonResource("berry_models")
    override val type = ResourceType.CLIENT_RESOURCES
    override val observable = SimpleObservable<BerryModelRepository>()
    override val gson: Gson = TexturedModel.GSON
    override val typeToken: TypeToken<TexturedModel> = TypeToken.get(TexturedModel::class.java)
    override val resourcePath = "bedrock/berries"
    private val models = hashMapOf<Identifier, ModelPart>()

    override fun sync(player: ServerPlayerEntity) {}

    override fun reload(data: Map<Identifier, TexturedModel>) {
        this.models.clear()
        data.forEach { (identifier, model) ->
            var textureName = "cobblemon:flower"
            if (identifier.endsWith("berry.geo")) {
                textureName = identifier.toString()
                    .substring(0, identifier.toString().length - "_berry.geo".length)
            }
            val atlas = CobblemonAtlases.BERRY_SPRITE_ATLAS
            this.models[identifier] = model.createFlywheelModel(
                atlas,
                Identifier.tryParse(textureName)!!,
                identifier.toString()
            )
        }
        Cobblemon.LOGGER.info("Loaded {} berry models", this.models.size)
    }
    fun modelOf(identifier: Identifier) = this.models[identifier]
}