/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.render.models.blockbench.bedrock.animation

import com.cablemc.pokemod.common.Pokemod
import com.cablemc.pokemod.common.util.fromJson
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
        val stream = Pokemod::class.java.getResourceAsStream("/assets/${Pokemod.MODID}/bedrock/animations/$fileName") ?: throw IllegalStateException("animation file $fileName could not be found")
        val animationGroup = gson.fromJson<BedrockAnimationGroup>(stream.reader())
        animationGroup.animations.forEach { (name, animation) -> animations[name] = animation }
    }

    fun clear() {
        animations.clear()
    }
}