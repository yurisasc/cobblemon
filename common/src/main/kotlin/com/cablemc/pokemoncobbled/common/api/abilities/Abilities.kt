package com.cablemc.pokemoncobbled.common.api.abilities

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.data.JsonDataRegistry
import com.cablemc.pokemoncobbled.common.api.reactive.SimpleObservable
import com.cablemc.pokemoncobbled.common.pokemon.abilities.HiddenAbility
import com.cablemc.pokemoncobbled.common.util.asTranslated
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import kotlin.io.path.Path
import net.minecraft.resource.ResourceType
import net.minecraft.text.MutableText
import net.minecraft.util.Identifier

/**
 * Registry for all known Abilities
 */
object Abilities : JsonDataRegistry<AbilityTemplate> {
    override val id = cobbledResource("abilities")
    override val type = ResourceType.SERVER_DATA
    override val observable = SimpleObservable<Abilities>()
    override val typeToken: TypeToken<AbilityTemplate> = TypeToken.get(AbilityTemplate::class.java)
    override val resourcePath = Path("abilities")
    override val gson: Gson = GsonBuilder()
        .disableHtmlEscaping()
        .setLenient()
        .setPrettyPrinting()
        .registerTypeAdapter(MutableText::class.java, MutableTextAdapter)
        .create()

    private val allAbilities = mutableListOf<AbilityTemplate>()
    private val abilityMap = mutableMapOf<String, AbilityTemplate>()

    object MutableTextAdapter : JsonDeserializer<MutableText> {
        override fun deserialize(json: JsonElement, type: Type, ctx: JsonDeserializationContext) = json.asString.asTranslated()
    }

    override fun reload(data: Map<Identifier, AbilityTemplate>) {
        PotentialAbility.interpreters.clear()
        PotentialAbility.interpreters.add(CommonAbility.interpreter)
        PotentialAbility.interpreters.add(HiddenAbility.interpreter)

        allAbilities.clear()
        abilityMap.clear()

        data.forEach { (identifier, abilityTemplate) ->
            allAbilities.add(abilityTemplate)
            abilityMap[identifier.path] = abilityTemplate
        }

        PokemonCobbled.LOGGER.info("Loaded {} abilities", this.allAbilities.size)
        this.observable.emit(this)
    }

    fun register(ability: AbilityTemplate): AbilityTemplate {
        abilityMap[ability.name.lowercase()] = ability
        allAbilities.add(ability)
        return ability
    }

    fun first() = allAbilities.first()
    fun get(name: String) = abilityMap[name.lowercase()]
    fun getOrException(name: String) = get(name)!!
    fun count() = allAbilities.size
}