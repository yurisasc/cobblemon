package com.cablemc.pokemod.common.advancement.criterion

import com.cablemc.pokemod.common.api.pokemon.PokemonProperties
import com.cablemc.pokemod.common.pokemon.Pokemon
import com.cablemc.pokemod.common.util.pokemodResource
import com.google.gson.JsonObject
import net.minecraft.advancement.criterion.AbstractCriterion
import net.minecraft.advancement.criterion.AbstractCriterionConditions
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer
import net.minecraft.predicate.entity.EntityPredicate
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

/**
 * A criteria that is triggered when a player picks a starter.
 *
 * @author Licious
 * @since October 26th, 2022
 */
class PickStarterCriterion : AbstractCriterion<PickStarterCriterion.Conditions>() {

    override fun getId(): Identifier = ID

    override fun conditionsFromJson(
        obj: JsonObject,
        playerPredicate: EntityPredicate.Extended,
        predicateDeserializer: AdvancementEntityPredicateDeserializer
    ): Conditions {
        if (obj.has("properties")) {
            val properties = PokemonProperties.parse(obj.get("properties").asString)
            return Conditions(playerPredicate, properties)
        }
        return Conditions(playerPredicate, null)
    }

    fun trigger(player: ServerPlayerEntity, pickedStarter: Pokemon) {
        this.trigger(player) { predicate -> predicate.matches(pickedStarter) }
    }

    class Conditions(entity: EntityPredicate.Extended, private val properties: PokemonProperties?) : AbstractCriterionConditions(ID, entity) {

        override fun toJson(predicateSerializer: AdvancementEntityPredicateSerializer?): JsonObject {
            val json = super.toJson(predicateSerializer)
            if (this.properties != null) {
                json.addProperty("properties", this.properties.originalString)
            }
            return json
        }

        fun matches(pickedStarter: Pokemon) = this.properties?.matches(pickedStarter) ?: true

    }

    companion object {

        private val ID = pokemodResource("pick_starter")

    }

}