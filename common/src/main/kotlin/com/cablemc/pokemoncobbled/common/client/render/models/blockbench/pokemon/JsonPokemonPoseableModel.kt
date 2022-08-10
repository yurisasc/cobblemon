package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.PoseableEntityState
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.StatefulAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.HeadedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.ModelFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.Pose
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.withPosition
import com.cablemc.pokemoncobbled.common.entity.PoseType
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.util.adapters.Vec3dAdapter
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import com.google.gson.InstanceCreator
import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class JsonPokemonPoseableModel(override val rootPart: ModelPart) : PokemonPoseableModel(), HeadedFrame {
    companion object {
        val gson = GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .registerTypeAdapter(Vec3d::class.java, Vec3dAdapter)
            .setExclusionStrategies(JsonModelExclusion)
            .registerTypeAdapter(TypeToken.getParameterized(StatefulAnimation::class.java, PokemonEntity::class.java, ModelFrame::class.java).type, StatefulAnimationAdapter)
            .registerTypeAdapter(Pose::class.java, PoseAdapter)
            .registerTypeAdapter(JsonPokemonPoseableModel::class.java, JsonPokemonPoseableModelAdapter)
            .create()

//        fun load()
    }

    override fun registerPoses() {}

    @SerializedName("head")
    val headJoint: String? = null

    override val head: ModelPart by lazy { headJoint?.let { getPart(it) } ?: rootPart }


    @SerializedName("portraitScale")
    private val _portraitScale: Float? = null
    @SerializedName("portraitTranslation")
    private val _portraitTranslation: Vec3d? = null

    @SerializedName("profileScale")
    private val _profileScale: Float? = null
    @SerializedName("profileTranslation")
    private val _profileTranslation: Vec3d? = null

    override val portraitScale
        get() = _portraitScale ?: 1F
    override val portraitTranslation
        get() = _portraitTranslation ?: Vec3d(0.0, 0.0, 0.0)

    override val profileScale
        get() = _profileScale ?: 1F
    override val profileTranslation
        get() = _profileTranslation ?: Vec3d(0.0, 0.0, 0.0)

    val faint: StatefulAnimation<PokemonEntity, ModelFrame>? = null

    override fun getFaintAnimation(pokemonEntity: PokemonEntity, state: PoseableEntityState<PokemonEntity>) = faint


    object JsonModelExclusion: ExclusionStrategy {
        override fun shouldSkipField(f: FieldAttributes): Boolean {
            return f.declaringClass.simpleName !in listOf(
                "JsonPokemonPoseableModel",
                "PoseableEntityModel",
                "Pose"
            )
        }

        override fun shouldSkipClass(clazz: Class<*>): Boolean {
            return false
        }

    }

    object StatefulAnimationAdapter : JsonDeserializer<StatefulAnimation<PokemonEntity, ModelFrame>> {
        override fun deserialize(json: JsonElement, type: Type, ctx: JsonDeserializationContext): StatefulAnimation<PokemonEntity, ModelFrame> {
            json as JsonPrimitive
            val animString = json.asString
            val splits = animString.replace("bedrock(", "").replace(")", "").split(",").map(String::trim)
            val file = splits[0]
            val animation = splits[1]
            return JsonPokemonPoseableModelAdapter.model!!.bedrockStateful(file, animation)
        }
    }

    object JsonPokemonPoseableModelAdapter : InstanceCreator<JsonPokemonPoseableModel> {
        var modelPart: ModelPart? = null
        var model: JsonPokemonPoseableModel? = null
        override fun createInstance(type: Type): JsonPokemonPoseableModel {
            return JsonPokemonPoseableModel(modelPart!!).also {
                model = it
                it.loadAllNamedChildren(modelPart!!)
            }
        }
    }

    object PoseAdapter : JsonDeserializer<Pose<PokemonEntity, ModelFrame>> {
        override fun deserialize(json: JsonElement, type: Type, ctx: JsonDeserializationContext): Pose<PokemonEntity, ModelFrame> {
            val model = JsonPokemonPoseableModelAdapter.model!!
            val obj = json as JsonObject
            val poseName = obj.get("poseName").asString
            val poseTypes = (obj.get("poseTypes")?.asJsonArray?.map { name ->
                PoseType.values().find { it.name.lowercase() == name.asString.lowercase() }
                    ?: throw IllegalArgumentException("Unknown pose type ${name.asString}")
            } ?: emptyList()) + if (obj.get("allPoseTypes")?.asBoolean == true) PoseType.values().toList() else emptyList()
            val transformTicks = obj.get("transformTicks")?.asInt ?: 10

            val transformedParts = obj.get("transformedParts")?.asJsonArray?.map {
                it as JsonObject
                val partName = it.get("part").asString
                val part = model.getPart(partName)
                val rotation = it.get("rotation")?.asJsonArray?.let { Vec3d(it[0].asDouble, it[1].asDouble, it[2].asDouble) } ?: Vec3d.ZERO
                val position = it.get("position")?.asJsonArray?.let { Vec3d(it[0].asDouble, it[1].asDouble, it[2].asDouble) } ?: Vec3d.ZERO
                return@map part.withPosition(position.x, position.y, position.z).withRotationDegrees(rotation.x, rotation.y, rotation.z)
            }?.toTypedArray() ?: arrayOf()

            val idleAnimations = (obj.get("animations")?.asJsonArray ?: JsonArray()).asJsonArray.mapNotNull {
                val animString = it.asString
                if (animString == "look") {
                    return@mapNotNull JsonPokemonPoseableModelAdapter.model!!.singleBoneLook<PokemonEntity>()
                } else if (animString.startsWith("bedrock")) {
                    val split = animString.replace("bedrock(", "").replace(")", "").split(",").map(String::trim)
                    return@mapNotNull model.bedrock(file = split[0], animation = split[1])
                }
                return@mapNotNull null
            }.toTypedArray()

            return Pose(
                poseName = poseName,
                poseTypes = poseTypes.toSet(),
                condition = { it.getPoseType() in poseTypes },
                transformTicks =  transformTicks,
                idleAnimations = idleAnimations,
                transformedParts = transformedParts
            )
        }
    }
}