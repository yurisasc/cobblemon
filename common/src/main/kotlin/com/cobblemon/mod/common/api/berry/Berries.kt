package com.cobblemon.mod.common.api.berry

import com.cobblemon.mod.common.api.data.JsonDataRegistry
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.util.adapters.FloatNumberRangeAdapter
import com.cobblemon.mod.common.util.cobblemonResource
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import net.minecraft.predicate.NumberRange
import net.minecraft.resource.ResourceType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

object Berries : JsonDataRegistry<Berry> {

    override val id: Identifier = cobblemonResource("berries")
    override val type: ResourceType = ResourceType.SERVER_DATA
    override val observable = SimpleObservable<Berries>()

    override val gson = GsonBuilder()
        .disableHtmlEscaping()
        .setPrettyPrinting()
        .registerTypeAdapter(NumberRange.FloatRange::class.java, FloatNumberRangeAdapter)
        .create()
    override val typeToken: TypeToken<Berry> = TypeToken.get(Berry::class.java)
    override val resourcePath = "berries"

    private val defaults = hashMapOf<Identifier, Berry>()
    private val custom = hashMapOf<Identifier, Berry>()

    val PECHA
        get() = this.byName("pecha")

    override fun reload(data: Map<Identifier, Berry>) {
        this.custom.clear()
        // ToDo once datapack berries are implemented load them here
    }

    init {
        this.create("pecha", 2..4, 3..3, NumberRange.FloatRange.between(0.8, 1.0), 1..1, NumberRange.FloatRange.between(0.8, 1.0), 1..1,Flavor.SWEET to 10)
    }

    // There's nothing to sync for clients atm
    override fun sync(player: ServerPlayerEntity) {}

    fun all() = this.defaults.filterKeys { !this.custom.containsKey(it) }.values + this.custom.values

    private fun create(
        name: String,
        baseYield: IntRange,
        lifeCycles: IntRange,
        temperatureRange: NumberRange.FloatRange,
        temperatureBonusYield: IntRange,
        downfallRange: NumberRange.FloatRange,
        downfallBonusYield: IntRange,
        vararg flavors: Pair<Flavor, Int>
    ) {
        val berry = Berry(cobblemonResource(name), baseYield, lifeCycles, temperatureRange, temperatureBonusYield, downfallRange, downfallBonusYield, flavors.toMap())
        this.defaults[berry.identifier] = berry
    }

    private fun byName(name: String): Berry {
        val identifier = cobblemonResource(name)
        return this.custom[identifier] ?: this.defaults[identifier]!!
    }

}