package com.cablemc.pokemoncobbled.common.util.adapters

import com.cablemc.pokemoncobbled.common.api.moves.MoveTemplate
import com.cablemc.pokemoncobbled.common.api.moves.Moves
import com.google.gson.*
import java.lang.reflect.Type

/**
 * Saves and loads [MoveTemplate]s with just their name as a representation.
 * This does not register any additional templates and instead looks them up using [Moves.getByName].
 *
 * @author Licious
 * @since March 21st, 2022
 */
object CobbledLazyMoveTemplateAdapter : JsonDeserializer<MoveTemplate>, JsonSerializer<MoveTemplate> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): MoveTemplate? = Moves.getByName(json.asString)

    override fun serialize(src: MoveTemplate, typeOfSrc: Type, context: JsonSerializationContext) = JsonPrimitive(src.name)

}