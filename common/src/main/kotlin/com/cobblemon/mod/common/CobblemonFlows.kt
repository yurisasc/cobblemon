package com.cobblemon.mod.common

import com.cobblemon.mod.common.api.data.DataRegistry
import com.cobblemon.mod.common.api.molang.ExpressionLike
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourceType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

object CobblemonFlows : DataRegistry {
    override val id = cobblemonResource("flows")
    override val observable = SimpleObservable<CobblemonFlows>()
    override val type = ResourceType.SERVER_DATA
    override fun sync(player: ServerPlayerEntity) {}

    val flows = hashMapOf<Identifier, List<ExpressionLike>>()

    override fun reload(manager: ResourceManager) {
        TODO("Not yet implemented")
    }
}