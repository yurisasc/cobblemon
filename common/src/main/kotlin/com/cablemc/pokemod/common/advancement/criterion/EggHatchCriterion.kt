package com.cablemc.pokemod.common.advancement.criterion

import com.cablemc.pokemod.common.util.pokemodResource
import com.google.gson.JsonObject
import net.minecraft.advancement.criterion.AbstractCriterion
import net.minecraft.advancement.criterion.AbstractCriterionConditions
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer
import net.minecraft.predicate.entity.EntityPredicate
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

class EggHatchCriterion : AbstractCriterion<EggHatchCriterion.Conditions>() {

    override fun getId(): Identifier = ID

    override fun conditionsFromJson(
        obj: JsonObject,
        playerPredicate: EntityPredicate.Extended,
        predicateDeserializer: AdvancementEntityPredicateDeserializer
    ): Conditions {
        if (obj.has("count")) {
            return Conditions(playerPredicate, obj.get("count").asInt)
        }
        return Conditions(playerPredicate, 0)
    }

    fun trigger(player: ServerPlayerEntity, count: Int) {
        this.trigger(player) { predicate -> predicate.matches(count) }
    }

    class Conditions(entity: EntityPredicate.Extended, private val count: Int) : AbstractCriterionConditions(ID, entity) {

        override fun toJson(predicateSerializer: AdvancementEntityPredicateSerializer?): JsonObject {
            val json = super.toJson(predicateSerializer)
            json.addProperty("count", this.count)
            return json
        }

        fun matches(totalCount: Int) = this.count == totalCount
    }

    companion object {

        private val ID = pokemodResource("eggs_hatched")

    }
}