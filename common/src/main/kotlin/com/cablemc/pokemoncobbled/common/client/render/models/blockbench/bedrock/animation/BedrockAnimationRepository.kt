package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.bedrock.animation

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.util.fromJson
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder


/**
 * Handles the loading and retrieval of bedrock animations.
 *
 * @author landonjw
 * @since January 5, 2022
 */
object BedrockAnimationRepository {

    private val gson = GsonBuilder()
        .disableHtmlEscaping()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .registerTypeAdapter(BedrockAnimation::class.java, BedrockAnimationAdapter)
        .create()

    private val animations: MutableMap<String, BedrockAnimation> = mutableMapOf()

    fun getAnimation(fileName: String, animationName: String): BedrockAnimation {
        if (animations[animationName] == null) {
            loadAnimationsFromFile(fileName)
        }
        return animations[animationName] ?: throw IllegalArgumentException("animation is not in specified file")
    }

    fun loadAnimationsFromFile(fileName: String) {
        val stream = PokemonCobbled::class.java.getResourceAsStream("/assets/${PokemonCobbled.MODID}/geo/animations/$fileName") ?: throw IllegalStateException("animation file $fileName could not be found")
        val animationGroup = gson.fromJson<BedrockAnimationGroup>(stream.reader())
        animationGroup.animations.forEach { (name, animation) -> animations[name] = animation }
    }

    fun clear() {
        animations.clear()
    }
}