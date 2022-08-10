package com.cablemc.pokemoncobbled.common.api.moves

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.data.JsonDataRegistry
import com.cablemc.pokemoncobbled.common.api.moves.adapters.DamageCategoryAdapter
import com.cablemc.pokemoncobbled.common.api.moves.categories.DamageCategory
import com.cablemc.pokemoncobbled.common.api.reactive.SimpleObservable
import com.cablemc.pokemoncobbled.common.api.types.ElementalType
import com.cablemc.pokemoncobbled.common.api.types.adapters.ElementalTypeAdapter
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlin.io.path.Path
import net.minecraft.resource.ResourceType
import net.minecraft.util.Identifier

/**
 * Registry for all known Moves
 */
object Moves : JsonDataRegistry<MoveTemplate> {
    override val gson = GsonBuilder()
        .registerTypeAdapter(DamageCategory::class.java, DamageCategoryAdapter)
        .registerTypeAdapter(ElementalType::class.java, ElementalTypeAdapter)
        .setLenient()
        .disableHtmlEscaping()
        .create()

    override val id = cobbledResource("moves")
    override val type = ResourceType.SERVER_DATA
    override val typeToken: TypeToken<MoveTemplate> = TypeToken.get(MoveTemplate::class.java)
    override val resourcePath = Path("moves")
    override val observable = SimpleObservable<Moves>()

    private val allMoves = mutableMapOf<String, MoveTemplate>()
    override fun reload(data: Map<Identifier, MoveTemplate>) {
        this.allMoves.clear()
        data.forEach { (identifier, moveTemplate) -> this.allMoves[identifier.path] = moveTemplate }
        PokemonCobbled.LOGGER.info("Loaded {} moves", this.allMoves.size)
        this.observable.emit(this)
    }

    fun getByName(name: String) = allMoves[name.lowercase()]
    fun getByNameOrDummy(name: String) = allMoves[name.lowercase()] ?: MoveTemplate.dummy(name.lowercase())
    fun getExceptional() = getByName("tackle") ?: allMoves.values.random()
    fun count() = allMoves.size
    fun names(): Collection<String> = this.allMoves.keys.toSet()
}