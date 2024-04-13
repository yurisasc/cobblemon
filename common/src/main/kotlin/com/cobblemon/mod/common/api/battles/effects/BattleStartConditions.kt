package com.cobblemon.mod.common.api.battles.effects

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.conditional.RegistryLikeCondition
import com.cobblemon.mod.common.api.data.JsonDataRegistry
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.api.spawning.TimeRange
import com.cobblemon.mod.common.util.adapters.BiomeLikeConditionAdapter
import com.cobblemon.mod.common.util.adapters.IdentifierAdapter
import com.cobblemon.mod.common.util.adapters.IntRangesAdapter
import com.cobblemon.mod.common.util.cobblemonResource
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import net.minecraft.resource.ResourceType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.world.biome.Biome

object BattleStartConditions : JsonDataRegistry<BattleStartCondition> {

    override val id = cobblemonResource("start_conditions")
    override val type = ResourceType.SERVER_DATA
    override val observable = SimpleObservable<BattleStartConditions>()

    override val gson = GsonBuilder()
        .disableHtmlEscaping()
        .setPrettyPrinting()
        .registerTypeAdapter(Identifier::class.java, IdentifierAdapter)
        .registerTypeAdapter(TimeRange::class.java, IntRangesAdapter(TimeRange.timeRanges) { TimeRange(*it) })
        .registerTypeAdapter(TypeToken.getParameterized(RegistryLikeCondition::class.java, Biome::class.java).type, BiomeLikeConditionAdapter)
        .create()

    override val typeToken = TypeToken.get(BattleStartCondition::class.java)
    override val resourcePath = "start_conditions"

    private val conditions = hashMapOf<Identifier, BattleStartCondition>()

    override fun reload(data: Map<Identifier, BattleStartCondition>) {
        this.conditions.clear()
        data.forEach { id, condition ->
            try {
                condition.identifier = id
                this.conditions[id] = condition
            } catch (e: Exception) {
                Cobblemon.LOGGER.error("Skipped loading battle start condition: {}", condition.identifier)
            }
            Cobblemon.LOGGER.info("Loaded {} battle start conditions", this.conditions.size)
            this.observable.emit(this)
        }
    }

    override fun sync(player: ServerPlayerEntity) {
        // no need for the client to know about these since it's all handled server-side
    }

    /**
     * Gets all loaded [BattleStartCondition]s.
     */
    fun all() = this.conditions.values.toList()

}